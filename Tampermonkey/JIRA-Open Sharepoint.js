// ==UserScript==
// @name         JIRA - Open Sharepoint
// @namespace
// @version      0.1
// @description  Open Sharepoint File systems so that we can upload the attachments
// @author       YataoWang
// @match        https://cdatajira.atlassian.net/*
// ==/UserScript==

var monthMap = {0: "January", 1: "February", 2: "March", 3: "April", 4: "May", 5: "June", 6: "July", 7: "August", 8: "September", 9: "October", 10: "November", 11: "December"};

function getFullYear() {
  var date = new Date();
  return date.getFullYear();
}

function getMonthStr() {
  var date = new Date();
  return monthMap[date.getMonth()];
}

function getSharepointURL() {
  return "https://cdata0.sharepoint.com/sites/jira-files/Shared Documents/Forms/AllItems.aspx?viewid=f540b7bb-4d96-4718-bb4d-b8e070cf9e9a&id=/sites/jira-files/Shared Documents/" + getFullYear() + "/" + getMonthStr();
}

function replaceAttachToOpenSharepoint() {
  jQuery(document).ready(function($) {
    $("#ak-main-content div button").attr("aria-label", function(index, oldValue) {
      if ("Attach" != oldValue) {
        return;
      }

      this.onclick = function() {
        var win = window.open(getSharepointURL(), '_blank');
        win && win.focus();
      };
    });
  }); 
}

function registerOpenSharepoint() {
  replaceAttachToOpenSharepoint();
}

document.addEventListener(
  'click',
  function() {
    setTimeout(registerOpenSharepoint, 600);
  },
  true
);

document.addEventListener(
  'load',
  function() {
    setTimeout(registerOpenSharepoint, 100);
  },
  true
);

(function() {
})();
