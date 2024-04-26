function setupQuestionManagement() {
    var editor;
    $("#deleteQuestionsConfirm").on("click", function () {
        $.ajax({
            url: "/player/admin/deleteAllQuestions",
            type: "GET",
            success: function () {
                $('#tab-content').load('/player/admin/questionManagement');
            }
        });
    });

    $("#saveButton").on("click", function () {
        $.ajax({
            url: "/player/admin/saveQuestions",
            type: "GET",
            data: {
                json: JSON.stringify(editor.get())
            },
            contentType: "application/json"
        });
    });

    $.ajax({
        url: '/JSON/QuestionTemplates.json',
        type: 'GET',
        success: function (data) {
            let json = data;
            const element = document.getElementById('jsonEditorElement');
            const options = {}
            editor = new JSONEditor(element, options)
            editor.set(json)
        },
        error: function (error) {
            console.log(error);
        }
    });
}