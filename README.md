Log
=============

This is a pdf creation plugin for Phonegap 3.3.0 / Cordova 3.3.1 supporting Android (>=2.3.3).
It writes all cordova javascript logs into a file in a public folder (Android: mnt/sdcard/, iOS: ~/Documents).

There are no JavaScript Methods (it´s hacked directly into the cordova log flow, "Usage below").

Installation
======
You may use phonegap CLI as follows:

<pre>
➜ phonegap local plugin add https://github.com/moderna/cordova-plugin-log.git
[phonegap] adding the plugin: https://github.com/moderna/cordova-plugin-log.git
[phonegap] successfully added the plugin
</pre>

Usage
====

ATTENTION: There is some manual work to be done before you can use this plugin!

Android:
---

1) In edit the following variables to your liking:
```
    private String logDir = ".at.modalog.cordova.plugin.log"; // prepending a dot "." makes the directory invisible
    private String logFileName = "CordovaChromeClient.log";
    private double logFileMaxSizeInKiloBytes = 512;
```

2) Open your apps main.java file and add this import statement:
```
    import at.modalog.cordova.plugin.log.CordovaChromeClientLog;
```

3) Update your main.java file:
```
    // deactivate the regular init
    // super.init();

    // modify init to use "CordovaChromeClientLog"
    CordovaWebView webView = this.makeWebView();
    super.init(webView, makeWebViewClient(webView), CordovaChromeClientLog.makeChromeClient(this, webView));
    // ...
```

iOS:
---

iOS is not yet supported.