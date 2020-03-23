// ==UserScript==
// @name         CSDN Filter
// @namespace    https://github.com/YataoWang/Tampermonkey
// @version      0.1
// @description  make CSDN concise
// @author       YataoWang
// @match        https://blog.csdn.net/*
// ==/UserScript==

(function() {
  var css = `.recommend-item-box, .recommend-right, #dmp_ad_58, aside, #container, .box-box-large, .meau-gotop-box, .csdn-toolbar, .tb_disnone, .csdn-side-toolbar{\
    display: none!important;\
  }\
  main{\
    width: 100%!important;\
  }`;
  var style = document.createElement("style");
  style.type = "text/css";
  style.appendChild(document.createTextNode(css));
  var head = document.getElementsByTagName("head")[0];
  head.appendChild(style);
})();
