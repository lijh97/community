function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2target(questionId, 1, content);
}

function comment(e) {
    var id = e.getAttribute("data-id");
    var content = $("#input-" + id).val();
    comment2target(id, 2, content);
}

function comment2target(targetId, type, content) {
    if (!content) {
        alert("不能回复空内容~~~");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: 'application/json',
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code === 200) {
                window.location.reload();
            } else if (response.code === 2003) {
                var isAccepted = confirm(response.message);
                if (isAccepted) {
                    window.open("https://github.com/login/oauth/authorize?client_id=bdb8b99644f6d0d5fabc&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                    window.localStorage.setItem("closeable", "true");
                }
            } else {
                alert(response.message());
            }
            console.log(response);
        },
        dataType: "json"
    });
}

function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);
    if (!comments.hasClass("in")) {
        if (comments.children().length === 1) {
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index, comment) {

                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var menuElement = $("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "glyphicon glyphicon-thumbs-up icon"
                    })).append($("<span/>", {
                        "class": "glyphicon glyphicon-comment icon"
                    })).append($("<span/>", {
                        "class": "pull-right",
                        html: moment(comment.gmtCreate).format('YYYY-MM-DD')
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        html: comment.user.name
                    })).append($("<div/>", {
                        html: comment.content
                    })).append(menuElement);

                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);
                    comments.prepend(commentElement);
                });

            });
        }
        //展开二级评论
        comments.addClass("in");
        e.classList.add("comment-icon-active");

    } else {
        comments.removeClass("in");
        e.classList.remove("comment-icon-active");
    }
}