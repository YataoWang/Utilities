// ==UserScript==
// @name         Runoob
// @namespace    https://github.com/YataoWang/Tampermonkey
// @version      0.1
// @description  make runoob concise
// @author       YataoWang
// @match        https://www.runoob.com/*
// @match        http://www.runoob.com/*
// ==/UserScript==

(function() {
  var css = `.mar-t50, .ad-box, .ad-box-large, .animations.init .col-sm-12{
    display: none !important;
  }
  .advertise-here, .sidebar-cate{
    display:inline !important;
  }
  .container.main{
    width: 100% !important;
  }
  .left-column{
    width: 15% !important;
  }
  .middle-column{
    width: 80% !important;
  }`;
  var style = document.createElement("style");
  style.type = "text/css";
  style.appendChild(document.createTextNode(css));
  var head = document.getElementsByTagName("head")[0];
  head.appendChild(style);
})();
