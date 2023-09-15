$('#replyBtn').on('click', replyFn);

$(document).ready(function(){
    replyList()
});

function replyFn() {

    const date = {
        'inqId': $('#inqId').val(),
        'reply': $('#reply').val()
    }

    $.ajax({
        type: 'POST',
        url: "/reply/write",
        data: date,
        success(res) {
            alert("작성완료");
        replyList();
        }
    });
    $('#reply').val("");
}

function replyList(){
    const inqId = $('#inqId').val();
    const data = {
        'inqId':inqId
    }

    $.ajax({
        url:"/reply/list",
        data: data,
        type:"get",
        success:function(res){
            var replyBody = $('#replyCon'); // id가 replyCon인 곳을 변수지정
            replyBody.html(''); // replyCon 초기화
            let list = "";
            if(res.length<1){
                list="등록된 댓글이 없습니다.";
            }else{

            $(res).each(function(){
                list = "<ul>";
                list+="<li>"+this.replyWriter+"</li>";
                list+="<li>"+this.createTime+"</li>";
                list+="<li>";
                list+="<div id='reCon"+this.id+"'>";
                list+="<span>"+this.reply+"</span>";
                list+='<input type="button" value="삭제" onclick="onDelete('+this.id+')">';
                list+="<input type='button' class='replyUpBtn' value='수정' onclick='showUpDate("+this.id+',"'+this.reply+'",'+this.inqId+")'>";
                list+="</div>";
                list+="<div id='showUp"+this.id+"'>";
                list+="</div>";
                list+="</li>";
                list+="</ul>";

                $('#replyCon').append(list); // replyCon에 추가
            });
            }
        }
    });
}

function onDelete(id){
    console.log(id)
    var dData = {
        'id':id
    }
    $.ajax({
        url:"/reply/delete",
        type:'POST',
        data:dData,
        success:function(res){
            if(res==1){
                alert("삭제성공!");
            }else{
                alert("삭제실패!");
            }
            replyList();
        },
        error:()=>{
            alert("Fail!!")
        }
    });
}

function showUpDate(id,reply,inqId){

    console.log(reply)
    console.log(id)
    console.log(inqId)

    const reId = $('#reCon'+id);
    console.log(reId.hasClass('hidden'));
    if(reId.hasClass('hidden')){
        reId.removeClass('hidden')
    }else{
        reId.addClass('hidden')
         $('#showUp'+id).html(
             "<textarea id='reply"+id+"'>"+reply+"</textarea>"
            +"<input type='button' class='replyUpBtn' value='완료' onclick='replyUpDate("+id+','+inqId+")'>"
            +"<input type='button' class='upXBtn' value='취소' onclick='upXDate("+inqId+")'>"
         );
    };
}

function replyUpDate(id,inqId){
    const data ={
        'reply':$('#reply'+id).val(),
        'id':id,
        'inqId':inqId
    };
    
    $.ajax({
        url:"/reply/up",
        type:'POST',
        data:data,
        success:function(res){
            if(res==1){
                alert("수정성공")
            }else{
                alert("수정실패")
            }
            replyList();

        }
    });

}