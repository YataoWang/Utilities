using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Text;

namespace AppService {

  public class CLICommand {
    private static readonly string TAB1 = "  ";
    private static readonly string TAB2 = TAB1 + TAB1;
    private static readonly string TAB3 = TAB1 + TAB2;
    private static readonly string TAB4 = TAB1 + TAB3;

    public static readonly string DEFAULT_OPT_PREFIX = "--";
    public static readonly string CLI_JAVA = "Java";

    public static readonly string CLI_HELP        = "help";
    public static readonly string CLI_INSTALL     = "install";
    public static readonly string CLI_UNINSTALL   = "uninstall";
    public static readonly string CLI_START       = "start";
    public static readonly string CLI_STOP        = "stop";
    public static readonly string CLI_RESTART     = "restart";
    public static readonly string CLI_SERVICENAME = "ServiceName";
    public static readonly string CLI_DISPLAYNAME = "DisplayName";
    public static readonly string CLI_DESCRIPTION = "Description";
    public static readonly string CLI_STARTMODE   = "StartMode";
    public static readonly string CLI_CLASSPATH   = "ClassPath";
    public static readonly string CLI_STARTPARAM  = "StartParams";
    public static readonly string CLI_STOPPARAM   = "StopParams";

    public static string BuildHelp() {
      StringBuilder builder = new StringBuilder();
      // Description
      builder.Append("DESCRIPTION:\r\n")
        .Append(TAB1).Append("This is a windows service, it can run other application as service.\r\n\r\n");
      // Usage
      builder.Append("USAGE:\r\n")
        .Append(TAB1).Append(Path.GetFileName(Assembly.GetExecutingAssembly().Location)).Append(" [command] [--ServiceName] <option1> <option2> ...\r\n\r\n");
      Options opts = CLICommand.CreateOptions();
      OptionBuilder ob = new OptionBuilder(opts, TAB1);
      // Install
      ob.BuildOption(builder,
        new string[] { CLI_INSTALL, CLI_DISPLAYNAME, CLI_DESCRIPTION, CLI_STARTMODE, CLI_CLASSPATH, CLI_STARTPARAM, CLI_STOPPARAM }, 
        new string[] { TAB1, TAB3, TAB3, TAB3, TAB3, TAB3, TAB3 });
      // Uninstall
      ob.BuildOption(builder, 
        new string[] { CLI_UNINSTALL }, 
        new string[] { TAB1 });
      // Start
      ob.BuildOption(builder, 
        new string[] { CLI_START }, 
        new string[] { TAB1 });
      // Stop
      ob.BuildOption(builder, 
        new string[] { CLI_STOP }, 
        new string[] { TAB1 });
      // Restart
      ob.BuildOption(builder, 
        new string[] { CLI_RESTART }, 
        new string[] { TAB1 });
      // Help
      ob.BuildOption(builder, 
        new string[] { CLI_HELP }, 
        new string[] { TAB1 });
      return builder.ToString();
    }

    public static Options CreateOptions() {
      Options opts = new Options();
      AddOption(opts, CLI_HELP, "Display the help", false);
      AddOption(opts, CLI_INSTALL, "Install the service", false);
      AddOption(opts, CLI_UNINSTALL, "Uninstall the service", false);
      AddOption(opts, CLI_START, "Start the service", false);
      AddOption(opts, CLI_STOP, "Stop the service", false);
      AddOption(opts, CLI_RESTART, "Restart the service", false);
      AddOption(opts, CLI_SERVICENAME, "Service name");
      AddOption(opts, CLI_DISPLAYNAME, "Service display name");
      AddOption(opts, CLI_DESCRIPTION, "Service description");
      AddOption(opts, CLI_STARTMODE, "Application start's mode(i.e, Java)");
      AddOption(opts, CLI_CLASSPATH, "Application's full path");
      AddOption(opts, CLI_STARTPARAM, "Start parameters. It's splited by semicolon if there are multiple parameters.");
      AddOption(opts, CLI_STOPPARAM, "Stop parameters. It's splited by semicolon if there are multiple parameters.");
      return opts;
    }

    private static void AddOption(Options opts, string optName, string optDesc) {
      AddOption(opts, optName, optDesc, true);
    }

    private static void AddOption(Options opts, string optName, string optDesc, bool hasArgs) {
      AddOption(opts, optName, optDesc, hasArgs, null);
    }

    private static void AddOption(Options opts, string optName, string optDesc, bool hasArgs, List<string> vals) {
      if (null == optName) {
        return;
      }

      opts.AddOption(new Option(optName, optDesc, hasArgs, vals));
    }

    class OptionBuilder {
      private Options _opts;
      private string _indent;
      private int _maxLen;

      public OptionBuilder(Options opts, string indent) {
        this._opts = opts;
        this._indent = indent;
        this._maxLen = CLIUtil.GetMaxOptNameLength(this._opts);
      }

      public void BuildOption(StringBuilder builder, string[] opts, string[] indents) {
        if (null == opts || null == indents) {
          return;
        }

        int length = opts.Length < indents.Length ? opts.Length : indents.Length;
        for (int i = 0; i < length; i++) {
          BuildOption(builder, this._opts.GetOption(opts[i]), indents[i]);
        }
      }

      public void BuildOption(StringBuilder builder, Option op, string indent) {
        builder.Append(this._indent).Append(indent)
          .Append(CLIUtil.BuildOptName(op.GetOpt())).Append(CLIUtil.CompleteSpace(this._maxLen, op.GetOpt(), indent))
          .Append(op.GetDescription()).Append("\r\n");
      }
    }
  }

  public class CLICommandParser {
    public static Options Parse(Options opts, string[] args) {
      return Parse(opts, args, false);
    }

    public static Options Parse(Options options, string[] args, bool ignoreUnknowOpt) {
      Options opts = new Options();
      List<Option> ops = options.GetOptions();
      foreach (Option op in ops) {
        op.ClearVals();
      }

      if (null == args) {
        args = new string[0];
      }

      int idx = 0;
      while (idx < args.Length) {
        string arg = args[idx];
        do {
          if (null == arg) {
            break;
          }

          if (!CLIUtil.IsValidArg(arg)) {
            break;
          }

          Option op = options.GetOption(arg);
          if (null == op) {
            if (!ignoreUnknowOpt) {
              throw new Exception(string.Format(CLIMessage.ERR_MSG_UNKNOW_OPTION, arg));
            } else {
              break;
            }
          }

          string next = idx < args.Length - 1 ? args[idx + 1] : null;
          bool isArgVal = (null != next) && !(CLIUtil.IsValidArg(next) && options.HasOption(next));
          if (isArgVal && op.HasArgs()) {
            op.AddVal(CLIUtil.TrimQuote(next));
            idx++;
          }

          opts.AddOption(op);
        } while (false);

        idx++;
      }

      return opts;
    }
  }

  public class Option {
    private string opt;
    private string description;
    private bool hasArgs;
    private List<string> vals;

    public Option(string opt, string description, bool hasArgs, List<string> vals) {
      this.opt = opt;
      this.description = description;
      this.hasArgs = hasArgs;
      this.vals = vals;
    }

    public string GetOpt() {
      return this.opt;
    }

    public void AddVal(string val) {
      if (null == this.vals) {
        this.vals = new List<string>();
      }

      this.vals.Add(val);
    }

    public string GetVal() {
      List<string> vals = GetVals();
      if (null != vals && vals.Count > 0) {
        return vals[0];
      }

      return null;
    }

    public List<string> GetVals() {
      return this.vals;
    }

    public string GetDescription() {
      return this.description;
    }

    public bool HasArgs() {
      return this.hasArgs;
    }

    public void ClearVals() {
      if (null != this.vals) {
        this.vals.Clear();
      }
    }

    public bool Equals(string opt) {
      if (null == opt) {
        return false;
      }

      return CLIUtil.EqualIgnoreCaseInvariant(opt, this.opt);
    }
  }

  public class Options {
    private List<Option> opts = new List<Option>();

    public Options AddOption(Option opt) {
      this.opts.Add(opt);
      return this;
    }

    public List<Option> GetOptions() {
      return this.opts;
    }

    public bool HasOption(string opt) {
      return null != GetOption(opt);
    }

    public Option GetOption(string opt) {
      if (null == opt) {
        return null;
      }

      opt = CLIUtil.TrimPrefix(opt);
      foreach (Option op in this.opts) {
        if (op.Equals(opt)) {
          return op;
        }
      }

      return null;
    }
  }

  public class CLIUtil {
    public static string GetCurrentDirectory() {
      return new FileInfo(Assembly.GetExecutingAssembly().Location).Directory.FullName;
    }

    public static string QuoteIdentity(string identity) {
      return "\"" + identity + "\"";
    }

    public static bool IsValidArg(string opt) {
      return opt.StartsWith(CLICommand.DEFAULT_OPT_PREFIX);
    }

    public static string BuildOptName(string opt) {
      if (null == opt) {
        return null;
      }

      if (!opt.StartsWith(CLICommand.DEFAULT_OPT_PREFIX)) {
        opt = CLICommand.DEFAULT_OPT_PREFIX + opt;
      }

      return opt;
    }

    public static string TrimPrefix(string str) {
      if (null != str) {
        if (str.StartsWith(CLICommand.DEFAULT_OPT_PREFIX)) {
          return str.Substring(CLICommand.DEFAULT_OPT_PREFIX.Length);
        }
      }

      return str;
    }

    public static string TrimQuote(string str) {
      if (null != str) {
        str = str.Trim();
        if (str.StartsWith("\"") && str.EndsWith("\"")) {
          str = str.Substring(1, str.Length - 1);
        }
      }

      return str;
    }

    public static string GetJavaPath() {
      string path = CLIEnvironment.GetEnvironmentVariable("JRE_HOME");
      if (IsNullOrEmpty(path)) {
        path = CLIEnvironment.GetEnvironmentVariable("JAVA_HOME");
      }

      if (IsNullOrEmpty(path)) {
        throw new Exception(string.Format(CLIMessage.ERR_MSG_NO_JAVA));
      }

      return Path.Combine(path, "bin\\java.exe");
    }

    public static int GetMaxOptNameLength(Options opts) {
      int len = 20;
      foreach (Option opt in opts.GetOptions()) {
        int tmp = opt.GetOpt().Length;
        len = (len > tmp) ? len : tmp;
      }

      return len;
    }

    public static string CheckServiceName(Options opts) {
      if (null == opts || !opts.HasOption(CLICommand.CLI_SERVICENAME)) {
        throw new Exception(string.Format(CLIMessage.ERR_MSG_MISSING_ARG_OPTION, CLICommand.CLI_SERVICENAME));
      }

      return opts.GetOption(CLICommand.CLI_SERVICENAME).GetVal();
    }

    public static string CheckClassPath(Options opts) {
      if (null == opts || !opts.HasOption(CLICommand.CLI_CLASSPATH)) {
        throw new Exception(string.Format(CLIMessage.ERR_MSG_MISSING_ARG_OPTION, CLICommand.CLI_CLASSPATH));
      }

      string classPath = opts.GetOption(CLICommand.CLI_CLASSPATH).GetVal();
      if (!CLIUtil.IsNullOrEmpty(classPath) && !Path.IsPathRooted(classPath)) {
        classPath = Path.Combine(CLIUtil.GetCurrentDirectory(), classPath);
      }
      if (!File.Exists(classPath)) {
        throw new Exception(string.Format(CLIMessage.ERR_MSG_INCORRECT_CLASSPATH, CLICommand.CLI_CLASSPATH, classPath));
      }

      return classPath;
    }

    public static string CheckStartMode(Options opts) {
      string startMode = CLICommand.CLI_JAVA;
      if (null != opts && opts.HasOption(CLICommand.CLI_STARTMODE)) {
        startMode = opts.GetOption(CLICommand.CLI_STARTMODE).GetVal();
      }

      return startMode;
    }

    public static string CompleteSpace(int len, string text) {
      return CompleteSpace(len, text, null);
    }

    public static string CompleteSpace(int len, string text, string indent) {
      StringBuilder sb = new StringBuilder();
      int txtLen = null == text ? 0 : text.Length;
      int spaceLen = (len > txtLen ? (len - txtLen) : (txtLen - len)) + 2 - (null == indent ? 0 : indent.Length);
      for (int i = 0; i < spaceLen; i++) {
        sb.Append(" ");
      }

      return sb.ToString();
    }

    public static bool IsNullOrEmpty(string text) {
      return (null == text || text.Length <= 0);
    }

    public static bool EqualIgnoreCaseInvariant(string left, string right) {
      return EqualIgnoreCaseInvariant(left, right, null);
    }

    public static bool EqualIgnoreCaseInvariant(string left, string right, CultureInfo cultureInfo) {
      if (null == left || null == right) {
        return left == right;
      }

      if (null == cultureInfo) {
        return 0 == string.Compare(left, right, StringComparison.InvariantCultureIgnoreCase);
      } else {
        return 0 == cultureInfo.CompareInfo.Compare(left, right);
      }
    }

    public static string ToLower(string text) {
      return CLIUtil.ToLower(text, null);
    }

    public static string ToLower(string text, CultureInfo cultureInfo) {
      if (null == text) {
        return text;
      }

      if (null == cultureInfo) {
        return text.ToLower();
      } else {
        return text.ToLower(cultureInfo);
      }
    }
  }

  public class CLIMessage {
    public static readonly string MSG_RUN_COMMAND = "Method: [{0}], StartMode: [{1}], ClassPath: [{2}], Parameters: [{3}]";
    public static readonly string ERR_MSG_UNKNOW_OPTION = "Unrecognized option: {0}.";
    public static readonly string ERR_MSG_MISSING_ARG_OPTION = "Missing argument for option: {0}.";
    public static readonly string ERR_MSG_SERVICE_EXIST = "Service '{0}' is already existed.";
    public static readonly string ERR_MSG_SERVICE_NOT_EXIST = "Service '{0}' is not existed.";
    public static readonly string ERR_MSG_INCORRECT_CLASSPATH = "The argument of '{0}' is incorrect, '{1}' is not existed.";
    public static readonly string ERR_MSG_SERVICE_NOT_STOP = "Start service failed. Service '{0}' is not in stopped status.";
    public static readonly string ERR_MSG_SERVICE_NOT_RUNNING = "Stop service failed. Service '{0}' is not in running status.";
    public static readonly string ERR_MSG_NO_JAVA = "Can not find the java application, please check 'JRE_HOME' or 'JAVA_HOME' environment variable.";
    public static readonly string ERR_MSG_NOT_SUPPORTED_PARAM = "Can not support the {0}({1}) yet.";
    public static readonly string ERR_MSG_COMMAND_FAILED = "Error: {0}";
    public static readonly string ERR_MSG_START_PROCESS_FAILED = "Start the inner process failed, please see the details in the application's log.";
    public static readonly string ERR_MSG_PROCESS_EXITED = "The running process has exited, PID={0}";
  }

  public static class CLIEnvironment {
    private static string[] cmdLine;
    private static LogWriter logger = new LogWriter();

    public static string[] CommandLine {
      get {
        return cmdLine;
      }
      set {
        cmdLine = value;
      }
    }

    public static LogWriter Logger {
      get {
        return logger;
      }
    }

    public static string GetEnvironmentVariable(string variable) {
      return Environment.GetEnvironmentVariable(variable);
    }
  }


  public class LogWriter {
    private static string ERROR   = "ERROR";
    private static string INFO    = "INFO";
    private static string WARNING = "WARNING";
    private static string MSG_FORMAT = "[{0}][{1}][PID: {2}]: {3}";

    public LogWriter() {
      Trace.Listeners.Add(new ConsoleTraceListener());
    }

    public void AddEventLog(EventLog log) {
      Trace.Listeners.Add(new EventLogTraceListener(log));
    }

    public void AddFileLog(string logfile) {
      StreamWriter writer = new StreamWriter(logfile, true);
      writer.AutoFlush = true;
      TextWriterTraceListener listener = new TextWriterTraceListener(writer);
      Trace.Listeners.Add(listener);
    }

    public void Error(string message) {
      string msg = string.Format(MSG_FORMAT,
                      DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss"),
                      ERROR,
                      Process.GetCurrentProcess().Id,
                      message);
      WriteLine(msg);
    }

    public void Warning(string message) {
      string msg = string.Format(MSG_FORMAT,
                      DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss"),
                      WARNING,
                      Process.GetCurrentProcess().Id,
                      message);
      WriteLine(msg);
    }

    public void Information(string message) {
      string msg = string.Format(MSG_FORMAT,
                      DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss"),
                      INFO,
                      Process.GetCurrentProcess().Id,
                      message);
      WriteLine(msg);
    }

    public void WriteLine(string message) {
      Trace.WriteLine(message);
    }
  }
}
