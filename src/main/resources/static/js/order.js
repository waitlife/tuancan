
    // /清空提示信息
    $(function() {
        $(".deleteAll").on('click', function() {
            $('#my-confirm').modal({
                relatedTarget: this,
                onConfirm: function(options) {
                    alert("处理清空的事件");
                }
            });
        });
    });
    /*单个商品*/
    /*    $(document).on("click", ".add_to_cart", function (e) {
       // $.toast("信息输入错误！", "cancel");

    });*/
var cartlist1 = " <div class=\"weui-cell weui-cell_swiped\">\n" +
                     "<div class=\"weui-cell__bd\">\n" +
                    "<div class=\"weui-cell\">\n" +
                    "<div class=\"weui-cell__hd\">\n<img src=\"";
var imgpath = "\" alt=\"\" style=\"width:20px;margin-right:5px;display:block\">\n" +
                    "</div>\n" +
                    "<div class=\"weui-cell__bd\">\n" +
                    "<p>";
var cartlist2 = "</p>\n" +
        "</div>\n" +
        "<div class=\"weui-cell__ft\">\n" +
        "<span class=\"price\">";
var cartlist3 = "</span>\n" +
                    "<div class=\"weui-count\">\n" +
                    "<p class=\"\">*1</p>\n" +
                    "</div>\n" +
                    "</div>\n" +
                    "</div>\n" +
                    "</div>\n" +
                    "<div class=\"weui-cell__ft\">\n" +
                    "<a class=\"weui-swiped-btn weui-swiped-btn_warn delete-swipeout rmcart\" href=\"javascript:\">删除</a>\n" +
                    "<a class=\"weui-swiped-btn weui-swiped-btn_default close-swipeout\" href=\"javascript:\">关闭</a>\n" +
                    "</div>\n" +
                    "</div>";
    var cartnum = 0;
    var cartpricesum=0;
    var recipeids="";
    function addtocart(recipeName,recipeIcon,recipeprice,e) {
        console.log($(e.currentTarget).attr("class"));
        console.log( recipeName+">"+recipeIcon+">"+recipeprice);
        recipeIcon="/icon/avatar4.png";
        cartmsg= $("#cart_msg");
        //cartmsg.show();
        cartnum= cartmsg.text();
        cartmsg.text(++cartnum);
        recipeids+=$(e.currentTarget).attr("lang")+',';
        console.log(recipeids);
        cartpricesum=cartpricesum+recipeprice;
        $("#cartlist").append(cartlist1+recipeIcon+imgpath+recipeName+cartlist2+recipeprice+cartlist3);
        $(".cartdesc").html(cartnum+"件商品, 共计 <strong>"+cartpricesum+"</strong> 元");

    }
    $(document).on('click', '.rmcart', function (e) {
        currprice=$(e.currentTarget).parents('.weui-cell').find('.price').text();
        $(e.currentTarget).parents('.weui-cell').remove();
        --cartnum;
        $("#cart_msg").text(cartnum);
        cartpricesum=cartpricesum-currprice;
        $(".cartdesc").html(cartnum+"件商品, 共计 <strong>"+cartpricesum+"</strong> 元");
    });
    $(document).on('click', '#paycart', function (e) {
        if (recipeids=="" || $.isEmptyObject(recipeids)){
            $.toast("你还没有选择菜单","cancel");
            return;
        }
        recipeids=recipeids.substring(0,recipeids.lastIndexOf(','));
        console.log(recipeids);
        $.showLoading("正在提交");
        $.ajax({
            url: "/order/save",
            type: "POST",
            data: {
                "recipeids":recipeids
            },
            error: function (error) {
                $.toast("网络错误", "cancel");
            },
            success: function (result) {
                $("#cartlist").find(".weui-cell_swiped").remove();
                $(".cartdesc").html("0件商品, 共计 <strong>0</strong> 元");
                $("#cart_msg").text(0);
                cartnum=0;
                cartpricesum=0;
                $.hideLoading();
            },
        });

    });
    /*查看点餐详情*/
    $(function () {
        $(".list-js").click(function () {
            $(".cart_info").slideToggle(200);

            if(($("#cover").css("display")) == "none"){
                $('#cover').css('display','block'); //显示遮罩层
                $("html,body").css("height","100%").css("overflow","hidden")

            }else{
                $('#cover').css('display','none'); //取消遮罩层
                $("html,body").css("height","100%").css("overflow","visible")
            }


        });
    });

    //tab切换
    $(function(){
        window.onload = function()
        {
            var $li = $('#tab li');
            var $ul = $('#content ul');
            $li.click(function(){
                var $this = $(this);
                var $t = $this.index();

                $li.removeClass();
                $this.addClass('current');
                //$ul.css('display','none');
               // $ul.eq($t).css('display','block');
            })
        }
    });

