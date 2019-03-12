var exec = require("cordova/exec");

var NativeIntunesdkWrapper = function() {};

NativeIntunesdkWrapper.prototype.initializeSDK = function(success,failure,emailId,clientId,tenantId,authToken) {
  cordova.exec(success,failure,"NativeIntunesdkWrapper","initializeInTuneSDK",[
    emailId,clientId,tenantId,authToken
  ]);
};

cordova.addConstructor(function() {
  if (!window.Cordova) {
    window.Cordova = cordova;
  }

  if (!window.plugins) window.plugins = {};
  window.plugins.NativeIntunesdkWrapper = new NativeIntunesdkWrapper();
});
