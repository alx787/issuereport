var setupreport = {};

setupreport.module = (function () {

    var showMessage = function() {
        console.log("=========== проверка ===========");
        return false;
    };




    return {
        showMessage:showMessage,
    };
}());



AJS.$(document).ready(function() {

    console.log("=========== проверка при загрузке ===========");

});