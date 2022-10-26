const userAjaxUrl = "admin/users/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: userAjaxUrl,
    updateTable: function () {
        $.get(userAjaxUrl, updateTableByData);
    }
}

// $(document).ready(function () {
$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "email"
                },
                {
                    "data": "roles"
                },
                {
                    "data": "enabled"
                },
                {
                    "data": "registered"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ]
        })
    );
});

function enable(userId, checkBox) {
    let url = ctx.ajaxUrl + userId;
    let enabled = checkBox.is(':checked');
    let data = "enabled=" + enabled;
    alert(enabled + " " + url);
    $.post(url, data)
        .done(function () {
            successNoty(enabled ? "Enabled" : "Disabled");
            // ctx.updateTable();
            $(checkBox).closest("tr").attr("data-user-enabled", enabled);
        })
        .fail($(document).ajaxError(function (event, jqXHR, options, jsExc) {
            failNoty(jqXHR);
        }));
}