'use strict';

services.factory('noauth', [
   function () {
     return {
       authorize: function (cb) {
           cb({});
       }
     };
   }
 ]);
