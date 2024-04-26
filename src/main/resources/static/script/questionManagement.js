function setupQuestionManagement(jsonContent) {
    $("#deleteQuestionsConfirm").on("click", function () {
        $.ajax({
            url: "/player/admin/deleteAllQuestions",
            type: "GET",
            success: function () {
                $('#tab-content').load('/player/admin/questionManagement');
            }
        });
    });

    const element = document.getElementById('jsonEditorElement');
    const options = {}
    const editor = new JSONEditor(element, options)
    editor.set(jsonContent)
    console.log(jsonContent)
}