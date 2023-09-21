$('#reviewBtn').on('click', reviewFn);

$(document).ready(function(){
    replyList()
});

function reviewFn() {
    const review = $('#review').val()
    const date = {
        'productId': $('#productId').val(),
        'review': $('#review').val()
    }

    if(review!=""){
        $.ajax({
            type: 'POST',
            url: "/review/write",
            data: date,
            success(res) {
                alert("작성완료");
            replyList();
            }
        });
    }else{
        alert("리뷰를 작성해주세요");
    }
        $('#review').val("");
}

function replyList(){
    const productId = $('#proId').val();
    const data = {
        'productId':productId
    }

    $.ajax({
        url:"/review/list",
        data: data,
        type:"get",
        success:function(res){
            var replyBody = $('#reviewCon'); // id가 reviewCon인 곳을 변수지정
            replyBody.html(''); // replyCon 초기화
            let list = "";
            if(res.length<1){
                list="등록된 댓글이 없습니다.";
            }else{

            $(res).each(function(){
                list = "<ul>";
                list+="<li class='writer'>"+this.reviewWriter+"</li>";
                list+="<li class='create'>"+this.createTime+"</li>";
                list+="<li>";
                list+="<div id='reCon"+this.id+"' class='review_content'>";
                list+="<span>"+this.review+"</span>";
                list+="<div class='Btn'>";
                list+='<input type="button" value="삭제" onclick="onDelete('+this.id+')">';
                list+="<input type='button' class='replyUpBtn' value='수정' onclick='showUpDate("+this.id+',"'+this.review+'",'+this.productId+")'>";
                list+="</div>";
                list+="</div>";
                list+="<div id='showUp"+this.id+"' class='show'>";
                list+="</div>";
                list+="</li>";
                list+="</ul>";
                 
                $('#reviewCon').append(list); // replyCon에 추가
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
        url:"/review/delete",
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

function showUpDate(id,review,productId){

    console.log(review)
    console.log(id)
    console.log(productId)
    const reviews = $('#reviews');
    const reId = $('#reCon'+id);
    console.log(reId.hasClass('content'));
    if(reId.hasClass('content')){
        reId.removeClass('content')
    }else{
        reviews.addClass('content')
        reId.addClass('content')
         $('#showUp'+id).html(
             "<textarea id='review"+id+"'>"+review+"</textarea>"
            +"<input type='button' class='replyUpBtn' value='완료' onclick='replyUpDate("+id+','+productId+")'>"
            +"<input type='button' class='upXBtn' value='취소' onclick='upXDate("+productId+")'>"
         );
    };
}

function replyUpDate(id,productId){
    const data ={
        'review':$('#review'+id).val(),
        'id':id,
        'productId':productId
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