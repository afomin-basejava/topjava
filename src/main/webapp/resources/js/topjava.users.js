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
    let enabled = checkBox.is(':checked');
    $.post(ctx.ajaxUrl + userId, "enabled=" + enabled)
        .done(function () {
            successNoty(enabled ? "Enabled" : "Disabled");
            // ctx.updateTable();
            // $(checkBox).closest("tr").attr("data-user-enabled", data-user-enabled);
            // $(chkbox).prop("checked", enabled);
            // $("div.row-form input[type='checkbox']").attr('data-user-enabled', enabled);
            $("div.row-form #checked").attr('data-user-enabled', enabled);
        })
        .fail(function (jqXHR) {
            notifyNoty("Oops! Something wong!" + " Error status: " + jqXHR.status);
            $("div.row-form #checked").attr('data-user-enabled', !enabled);
        });
}