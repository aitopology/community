$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	//获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title, "content":content},
		function(data){
			data = $.parseJSON(data);
			//模态显示提示
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				if(data.code == 0){
					window.location.reload();
				}
			}, 2000);
		}
	)
}