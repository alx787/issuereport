var setupreport = {};

setupreport.module = (function () {

    var showMessage = function() {
        console.log("=========== проверка ===========");
        return false;
    };


    // глобальная переменная модуля хранит урл инстанса
    var jiraBaseUrl = null;

    /////////////////////////////////////////////////////////
    // функция для получения урл из датапровайдера при первом обращении
    var getBaseUrl = function() {

        // инициализируем переменную при первом запуске
        if (jiraBaseUrl == null) {
            // The data key is [groupId].[artifactId]:[web-resource-key].[data-key]
            var JIRA_BASE_URL_DATA_KEY = 'ru.segezhagroup.alx.issuereport:issuereport-resources.web-data';

            // Call WRM.data.claim() to retrieve your data.
            var data_from_provider = WRM.data.claim(JIRA_BASE_URL_DATA_KEY);
            jiraBaseUrl = data_from_provider.home_url;
        }

        return jiraBaseUrl;
    };




    /////////////////////////////////////////////////////////
    // получить заполнить строку
    var renderRow = function (number, name, user, sheds, active) {

        var rowTemplate = '<tr>'
        + '<td headers="basic-number">__number__</td>'
        + '<td headers="basic-name">__name__</td>'
        + '<td headers="basic-users">__user__</td>'
        + '<td headers="basic-sheds">__sheds__</td>'
        + '<td headers="basic-active" style="cursor: pointer">'
        +     '__badge__'
        + '</td>'
        + '<td headers="basic-edit" style="cursor: pointer" onclick="setupreport.module.openReport(__number__);">'
        +     '<span class="aui-icon aui-icon-small aui-iconfont-new-edit">Edit</span>'
        + '</td>'
        + '<td headers="basic-view" style="cursor: pointer" onclick="setupreport.module.openDemo(__number__);">'
        +     '<span class="aui-icon aui-icon-small aui-iconfont-screen">View</span>'
        + '</td>'
        + '<td headers="basic-test" style="cursor: pointer">'
        +     '<span class="aui-icon aui-icon-small aui-iconfont-email">Test</span>'
        + '</td>'
        + '<td headers="basic-del" style="cursor: pointer" onclick="setupreport.module.showDeleteDialog(__number__);"'
        +     '<span class="aui-icon aui-icon-small aui-iconfont-trash">Delete</span>'
        + '</td>'
        + '</tr>';

        var rowStr = rowTemplate;
        rowStr = rowStr.replaceAll("__number__", number);
        rowStr = rowStr.replace("__name__", name);
        rowStr = rowStr.replace("__user__", user);
        rowStr = rowStr.replace("__sheds__", sheds);

        if (active) {
            rowStr = rowStr.replace("__badge__", '<aui-badge class="aui-badge-primary">Включено</aui-badge>');
        } else {
            rowStr = rowStr.replace("__badge__", '<aui-badge>Выключено</aui-badge>');

        };


        return rowStr;
    };


    /////////////////////////////////////////////////////////
    // получить заполнить строку
    var refreshDataInTable = function (jsonData) {
        var dataLength = jsonData.length;

        // if (dataLength == 0) {
        //     return false;
        // }

        // таблица объект
        var tableObj = AJS.$("#reports-setup tbody");

        // очистим строки таблицы
        AJS.$("#reports-setup tbody tr").remove();


        for (var i = 0; i < dataLength; i++) {
            var oneRow = renderRow(
                                        jsonData[i].taskid,
                                        jsonData[i].name,
                                        jsonData[i].username,
                                        jsonData[i].sheds,
                                        jsonData[i].active
                                    );

            tableObj.append(oneRow);


        }

    };

    /////////////////////////////////////////////////////////
    // заполнение таблицы данными с сервера
    var fillTable = function () {

        AJS.$.ajax({
            url: setupreport.module.getBaseUrl() + "/rest/issuereport/1.0/shedulers/getalltasks",
            type: 'get',
            dataType: 'json',
            // data: JSON.stringify(jsonObj),
            // async: false,
            // async: true,
            contentType: "application/json; charset=utf-8",
            success: function(data) {

                // console.log(data);
                refreshDataInTable(data);
                // if (data.status == "ok") {
                //     console.log("ok");
                //     refreshDataInTable(data);
                // }

            },
            error: function(data) {
                var myFlag = AJS.flag({
                    type: 'error',
                    body: 'Ошибка обновления списка отчетов',
                });

            },
        });

        return true;
    };


    /////////////////////////////////////////////////////////
    // проверка заполненности полей
    // возвращает названия незаполнненных полей, или пустая строка если все заполнено
    var checkEmptyFields = function () {

        var msg = "";

        if (AJS.$.trim(AJS.$("#editname").val()) == "") {
            msg = msg + "<p>название</p>";
        }

        if (AJS.$.trim(AJS.$("#editfilterstring").val()) == "") {
            msg = msg +"<p>запрос</p>";
        }

        if (AJS.$.trim(AJS.$("#editshedtime").val()) == "") {
            msg = msg + "<p>время</p>";
        }


        if (AJS.$("#edituser").auiSelect2("data") == null) {
            msg = msg + "<p>пользователь</p>";
        }

        if (AJS.$("#receivers option").size() == 0) {
            msg = msg + "<p>получатели</p>";
        }

        return msg;

    }


    /////////////////////////////////////////////////////////
    // открытие задачи
    var openReport = function(taskId) {
        // console.log(AJS.$(elemEdit).parent().find("td")[0].textContent);
        // console.log(elemEdit.parentElement.getElementsByTagName("td")[0].textContent);

        // var taskId = elemEdit.parentElement.getElementsByTagName("td")[0].textContent;

        AJS.$.ajax({
            url: getBaseUrl() + "/rest/issuereport/1.0/shedulers/gettask/" + taskId,
            type: 'get',
            dataType: 'json',
            // data: JSON.stringify(jsonObj),
            // async: false,
            // async: true,
            contentType: "application/json; charset=utf-8",
            success: function(data) {

                console.log(data);

                AJS.$("#task-edit-dialog h2.aui-dialog2-header-main").text("Изменить отчет № " + taskId);

                // переменная означающая режим открытия
                // add - добавить запись
                // edit - редактировать
                AJS.$("#editmode").val("edit");
                AJS.$("#tasknumber").val(taskId);


                // заполнение всех полей
                AJS.$("#editname").val(data.name);
                AJS.$("#editfilterstring").val(data.filterstring);

                var toggle = document.getElementById('editisactive');
                toggle.checked = data.active;

                AJS.$("#editshedtime").val(data.sheds);

                // $("#mySelect2").select2('data', { id:"elementID", text: "Hello!"});
                // для установки значения нужна информация в виде
                //     {
                //         "id": "testuser",
                //         "text": "Тестов Тест Тестович",
                //         "email": "testov@organuzatsia.com",
                //         "key": "JIRAUSER10101"
                //     }

                AJS.$("#edituser").auiSelect2("data", { id: data.userid, text: data.username, email: data.useremail, key: data.userkey});
                AJS.$("#receiveruser").auiSelect2("val", "");


                AJS.$('#receivers').find('option').remove();
                for (var i = 0; i < data.receivers.length; i++ ) {
                    AJS.$('#receivers').append($('<option value="' + data.receivers[i].key + '">' + data.receivers[i].email + '</option>'));
                }


                AJS.dialog2("#task-edit-dialog").show();




                // refreshDataInTable(data);
                // if (data.status == "ok") {
                //     console.log("ok");
                //     refreshDataInTable(data);
                // }

            },
            error: function(data) {
                var myFlag = AJS.flag({
                    type: 'error',
                    body: 'Ошибка при получении данных',
                });

            },
        });

    };


    /////////////////////////////////////////////////////////
    // действие над задачей, в зависимости от параметра actionString
    // add - добавить задачу
    // edit - редактировать задачу
    var createUpdateReport = function (actionString) {

        var msgReqFields = checkEmptyFields();

        if (msgReqFields != "") {
            var myFlag = AJS.flag({
                type: 'error',
                body: '<p><b>Не заполнены поля</b></p>' + msgReqFields,
                close: 'auto'
            });
            return false;
        }


        var jsonObj = {};

        jsonObj.taskid =        AJS.$("#tasknumber").val();
        jsonObj.name =          AJS.$("#editname").val();
        jsonObj.filterstring =  AJS.$("#editfilterstring").val();
        jsonObj.shedtime =      AJS.$("#editshedtime").val();

        var toggle = document.getElementById('editisactive');
        jsonObj.active = toggle.checked;

        jsonObj.userkey =       AJS.$("#edituser").auiSelect2("data").key;

        var receivers = [];

        var recArrVals =        AJS.$("#receivers option");
        var recArrSize = recArrVals.size();
        for (var i = 0; i < recArrSize; i++) {

            var oneRcv = {};
            oneRcv.key =    AJS.$(recArrVals[i]).val();
            oneRcv.email =  AJS.$(recArrVals[i]).text();

            receivers.push(oneRcv);

        };


        jsonObj.receivers = receivers;

//        {
//            name: "new task",
//            filterstring: "sdfsdf",
//            shedtime: "223ge",
//            active : "true",
//            receivers: [
//                {key:"1", email:"e1@mail.com", name: "alx"},
//                {key:"2", email:"e2@mail.com", name: "gle"}
//            ]
//        }


        var url_rest = "";

        if (actionString == "add") {
            url_rest = setupreport.module.getBaseUrl() + "/rest/issuereport/1.0/shedulers/newtask";
        };

        if (actionString == "edit") {
            url_rest = setupreport.module.getBaseUrl() + "/rest/issuereport/1.0/shedulers/updatetask";
        };

        if (url_rest == "") {
            var myFlag = AJS.flag({
                type: 'error',
                body: 'Не определено действие над задачей',
                close: 'auto'
            });

            return false;

        }


        AJS.$.ajax({
            url: url_rest,
            type: 'post',
            dataType: 'json',
            data: JSON.stringify(jsonObj),
            // async: false,
            // async: true,
            contentType: "application/json; charset=utf-8",
            success: function(data) {

         /////////////////////////////////////////////////////////
           if (data.status == "ok") {
                    var myFlag = AJS.flag({
                        type: 'success',
                        body: 'Обновлен отчет ' + data.taskid,
                        close: 'auto'
                    });

                    // закрываем окно если завершилось нормально
                    AJS.dialog2("#task-edit-dialog").hide();

                // console.log(data);
                fillTable();

                }

            },
            error: function(data) {
                var myFlag = AJS.flag({
                    type: 'error',
                    body: 'Произошла ошибка',
                    close: 'auto'
                });

            },
        });

        return true;
    }


    /////////////////////////////////////////////////////////
    // создание задачи с отчетом
    var createReport = function () {
        createUpdateReport("add");
    }

    /////////////////////////////////////////////////////////
    // обновить задачи с отчетом
    var editReport = function () {
        createUpdateReport("edit");
    }


    var showDeleteDialog = function (taskId) {

        // предварительно установим скрытый идентификатор задачи
        AJS.$("#deletenumber").val(taskId);
        AJS.$("#deleted-report").text("Удалить отчет " + taskId + " ?");

        AJS.dialog2("#delete-dialog").show();

    }


    /////////////////////////////////////////////////////////
    // удалить задачу с отчетом
    var deleteReport = function () {
        // console.log(taskId);

        var taskId = AJS.$("#deletenumber").val();

        AJS.$.ajax({
            url: setupreport.module.getBaseUrl() + "/rest/issuereport/1.0/shedulers/deletetask/" + taskId,
            type: 'get',
            dataType: 'json',
            // data: JSON.stringify(jsonObj),
            // async: false,
            // async: true,
            contentType: "application/json; charset=utf-8",
            success: function(data) {

                /////////////////////////////////////////////////////////
                if (data.status == "ok") {
                    var myFlag = AJS.flag({
                        type: 'success',
                        body: 'Удален отчет ' + taskId,
                        close: 'auto'
                    });

                    // закрываем окно если завершилось нормально
                    AJS.dialog2("#delete-dialog").hide();

                    // console.log(data);
                    fillTable();

                }

            },
            error: function(data) {
                var myFlag = AJS.flag({
                    type: 'error',
                    body: 'Произошла ошибка',
                    close: 'auto'
                });

            },
        });

        return true;
    }



    /////////////////////////////////////////////////////////
    // предварительный просмотр отчета
    var openDemo = function (taskId) {
        // предварительно установим скрытый идентификатор задачи
        // AJS.$("#deletenumber").val(taskId);
        // AJS.$("#deleted-report").text("Удалить отчет " + taskId + " ?");

        AJS.dialog2("#show-demo-dialog").show();
    }


    return {
        showMessage:showMessage,
        fillTable:fillTable,
        getBaseUrl:getBaseUrl,
        createReport:createReport,
        editReport:editReport,
        deleteReport:deleteReport,
        showDeleteDialog:showDeleteDialog,
        openDemo:openDemo,
        openReport:openReport,
    };
}());



AJS.$(document).ready(function() {

    // console.log("=========== проверка при загрузке ===========");

    // окно добавления отчета
    // открыть
    AJS.$("#button-add").click(function(e) {
        e.preventDefault();
        AJS.$("#task-edit-dialog h2.aui-dialog2-header-main").text("Добавить новый отчет");

        // переменная означающая режим открытия
        // add - добавить запись
        // edit - редактировать
        AJS.$("#editmode").val("add");
        AJS.$("#tasknumber").val("");


        // очистка всех полей
        AJS.$("#editname").val("");
        AJS.$("#editfilterstring").val("");

        var toggle = document.getElementById('editisactive');
        toggle.checked = false;

        AJS.$("#editshedtime").val("");

        AJS.$("#edituser").auiSelect2("val", "");
        AJS.$("#receiveruser").auiSelect2("val", "");

        AJS.$('#receivers').find('option').remove();

        AJS.dialog2("#task-edit-dialog").show();
    });


    // добавить получателя в список получателей
    AJS.$("#add-user-button").click(function (e) {
        e.preventDefault();
        // console.log(AJS.$("#receiveruser").auiSelect2("data"));

        var rcvUserVal = AJS.$("#receiveruser").auiSelect2("data");
        if (rcvUserVal != null) {
            // сначала проверим чтобы в списке не было такого же пользователя
            var recArrVals = AJS.$("#receivers option");
            var recArrSize = recArrVals.size();
            var findVal = false;
            for (var i = 0; i < recArrSize; i++) {
                if (AJS.$(recArrVals[i]).val() == rcvUserVal.key) {
                    findVal = true;
                }
            };

            if (!findVal) {
                AJS.$('#receivers').append($('<option value="' + rcvUserVal.key + '">' + rcvUserVal.email + '</option>'));
            };

        }

    });


    // удалить пользователя из списка
    AJS.$("#remove-user-button").click(function (e) {
        e.preventDefault();
        AJS.$("#receivers option:selected").remove()
    });

    // очистить список пользователей пользователя из списка
    AJS.$("#clear-user-button").click(function (e) {
        e.preventDefault();
        AJS.$("#receivers option").remove()
    });



    // кнопка ОК
    AJS.$("#edit-submit-button").click(function (e) {
        e.preventDefault();

        if (AJS.$("#editmode").val() == "add") {
            setupreport.module.createReport();
        };

        if (AJS.$("#editmode").val() == "edit") {
            setupreport.module.editReport();
        };


        // закрытие формы будет в функции создания или обновления
        // чтобы не закрыть форму в случае ошибок
        //AJS.dialog2("#task-edit-dialog").hide();
    });

    AJS.$("#edit-cancel-button").click(function (e) {
        e.preventDefault();
        AJS.dialog2("#task-edit-dialog").hide();
    });

    ///////////////////////////////////.java:192/////////////////
    // инициализация селекта для поля Пользователь
    AJS.$("#edituser").auiSelect2({
        // minimumInputLength: 0,
        ajax: {
            delay: 250,
            url: function(searchdata) {
                // return AJS.params.baseURL + "/rest/api/2/user/search?username=" + searchdata;
                return setupreport.module.getBaseUrl() + "/rest/api/2/user/search?username=" + searchdata;
            },

            // url: 'https://api.github.com/search/repositories',
            dataType: 'json',
            // data: function (term, page) {
            //     // зачем то надо еще не понял зачем
            //     return {q: term};
            // },
            results: function (data) {
                var retVal = [];
                for (var i = 0; i < data.length; i++) {
                    var jsonObj = {};
                    // console.log(data[i]);
                    jsonObj.id = data[i].name;
                    //jsonObj.id = i;
                    jsonObj.text = data[i].displayName;
                    jsonObj.email = data[i].emailAddress;
                    jsonObj.key = data[i].key;
                    retVal.push(jsonObj);
                }

                // Tranforms the top-level key of the response object from 'items' to 'results'
                return {
                    results: retVal
                };
            }
        }
    });

    ////////////////////////////////////////////////////
    // инициализация селекта для поля Добавить пользователя
    AJS.$("#receiveruser").auiSelect2({
        // minimumInputLength: 0,
        ajax: {
            delay: 250,
            url: function(searchdata) {
                // return AJS.params.baseURL + "/rest/api/2/user/search?username=" + searchdata;
                return setupreport.module.getBaseUrl() + "/rest/api/2/user/search?username=" + searchdata;
            },

            // url: 'https://api.github.com/search/repositories',
            dataType: 'json',
            // data: function (term, page) {
            //     // зачем то надо еще не понял зачем
            //     return {q: term};
            // },
            results: function (data, page) {
                var retVal = [];
                for (var i = 0; i < data.length; i++) {
                    var jsonObj = {};
                    // console.log(data[i]);
                    jsonObj.id = data[i].name;
                    //jsonObj.id = i;
                    jsonObj.text = data[i].displayName;
                    jsonObj.email = data[i].emailAddress;
                    jsonObj.key = data[i].key;
                    retVal.push(jsonObj);
                }

                // Tranforms the top-level key of the response object from 'items' to 'results'
                return {
                    results: retVal
                };
            }
        }
    });


    ///////////////////////////////////////////////////////
    // события кнопок формы удаления
    AJS.$("#delete-dialog-confirm").on('click', function (e) {
        e.preventDefault();
        setupreport.module.deleteReport();
    });

    AJS.$("#delete-dialog-cancel").on('click', function (e) {
        e.preventDefault();
        AJS.dialog2("#delete-dialog").hide();
    });


    ////////////////////////////////////////////////////
    // предпросмотр
    // AJS.$("#reports-setup tr td[headers='basic-active']").click(function(e) {
    //     e.preventDefault();
    //     AJS.dialog2("#show-demo-dialog").show();
    // });
    //


    setupreport.module.fillTable();

});