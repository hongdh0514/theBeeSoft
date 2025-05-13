let initialCondCode;
let initialInputVal;
let previousListPage = 0;
let searchPageNum = 0;
let searchFlag = false;

let titleFlag = true;

$(document).ready(function() {
    console.log("jQuery 로드 완료, DOM 준비됨");

    let boardListBox = $("#boardListBox");
    let boardDetailBox = $("#boardDetailBox");
    let boardWriteBox = $("#boardWriteBox");

    // 에러 메시지 알럿
    const errorMessage = $('body').data('error-message');
    if (typeof errorMessage === 'string' && errorMessage.trim() !== '') {
        alert(errorMessage);
    }

    // 처음 게시글 목록
    loadBoards(0);

    // 공통 게시글 목록 표시 및 페이징 처리 함수
    function displayBoardList(response, page) {
        $('#boardListBox table tbody').html(response);
        let totalPages = $(response).find('#totalPages').data('total-pages') || 1;
        updatePagination(totalPages, page);
        boardDetailBox.hide();
        boardWriteBox.hide();
        boardListBox.show();
    }

    // 게시글 리스트
    function loadBoards(page) {
        $.ajax({
            url: '/board/boardAll',
            type: 'GET',
            data: { page: page },
            dataType: 'html',
            success: function(response) {
                displayBoardList(response, page);
            },
            error: function(xhr, status, error) {
                console.error("loadBoards 오류:", status, error, xhr.responseText);
                alert('전체 게시글 목록을 불러오는 중 오류가 발생했습니다.');
            }
        });
    }

    // 페이징 처리
    function updatePagination(totalPages, currentPage) {
        let paginationHtml = '<nav><ul class="pagination">';
        let startPage = Math.max(0, currentPage - 5);
        let endPage = Math.min(totalPages, startPage + 10);

        if (currentPage > 0) {
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" data-page="${currentPage - 1}">이전</a></li>`;
        } else {
            paginationHtml += `<li class="page-item disabled"><span class="page-link">이전</span></li>`;
        }

        for (let i = startPage; i < endPage; i++) {
            paginationHtml += `<li class="page-item ${i === currentPage ? 'active' : ''}">
                <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
            </li>`;
        }

        if (currentPage < totalPages - 1) {
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" data-page="${currentPage + 1}">다음</a></li>`;
        } else {
            paginationHtml += `<li class="page-item disabled"><span class="page-link">다음</span></li>`;
        }

        paginationHtml += '</ul></nav>';
        $('#boardListBox .pagination-container').html(paginationHtml);
        scrollToTarget(0)
    }

    // 페이지 버튼 클릭
    $(document).on('click', '.page-link', function(e) {
        e.preventDefault();
        let page = $(this).data('page');
        if (searchFlag) {
            searchPageNum = page;
            console.log("검색 중 페이지 이동 요청: page =", page);
            handleSearch(initialCondCode, initialInputVal, searchPageNum);
        } else {
            previousListPage = page;
            console.log("페이지 이동 요청: page =", page);
            loadBoards(page);
        }
    });

    // 게시글 작성
    $(".newBtn").click(function(e) {
        e.preventDefault();
        $.ajax({
            type: "GET",
            url: "/board/new_t",
            success: function(fragment) {
                boardListBox.hide();
                boardDetailBox.hide();
                boardWriteBox.html(fragment).show();
            }
        });
    });

    // 게시글 상세 - title 버튼 클릭
    $(document).on('click', '#boardListBox .titleBtn', function(e) {
        e.preventDefault();
        let boardId = $(this).data('board-id');
        // previousListPage = getCurrentPage();
        loadBoardDetail(boardId);
        scrollToTarget(0)
    });

    // 게시글 상세 - comment 버튼 클릭
    $(document).on('click', '#boardListBox .comment_count', function(e) {
        e.preventDefault();

        let boardId = $(this).closest('td').find('.titleBtn').data('board-id');

        titleFlag = false;
        loadBoardDetail(boardId);
    });

    // 게시글 상세 정보 로드 및 표시 함수
    function loadBoardDetail(boardId) {
        console.log("게시글 조회 요청: ID = " + boardId);
        $.ajax({
            type: "GET",
            url: "/api/board/" + boardId,
            success: function(response) {
                if(response === "success") {
                    $.ajax({
                        type: "GET",
                        url: "/board/" + boardId + "/fragment",
                        success: function(fragment) {
                            boardListBox.hide();
                            boardWriteBox.hide();
                            boardDetailBox.html(fragment).show();
                            if(!titleFlag) {
                                $("#comment_content").focus();
                                let targetPosition =  $(".comment-section").offset().top - 30;
                                scrollToTarget(targetPosition);
                                titleFlag = true;
                            }
                        }
                    });
                } else if (response === "failure_not_found") {
                    alert("존재하지 않는 게시글입니다.");
                } else if (response === "failure_admin_or_writer") {
                    alert("작성자 혹은 운영자만 조회할 수 있습니다.");
                }
            },
            error: function(xhr, status, error) {
                console.error("AJAX 오류:", status, error, xhr.responseText);
                alert("[Ajax] 게시글 조회 중 오류가 발생했습니다.");
                window.location.href = "/board";
            }
        });
    }

    // 목록으로
    $(document).on('click', '.backToList', function(e) {
        e.preventDefault();
        boardDetailBox.empty().hide();
        boardWriteBox.empty().hide();

        if (searchFlag) {
            handleSearch(initialCondCode, initialInputVal, searchPageNum);
        } else {
            loadBoards(previousListPage);
        }
    });

    // 게시글 삭제 - 목록
    $(document).on('click', '#boardListBox .deleteBtn', function(event) {
        handleDelete(event, $(this).data('board-id'), $(this).closest('tr'));
    });

    // 게시글 삭제 - 상세
    $(document).on('click', '#boardDetailBox .deleteBtn', function(event) {
        handleDelete(event, $(this).data('board-id'), $("#boardListBox").find('a[data-board-id="' + $(this).data('board-id') + '"]').closest('tr'));
    });

    // 삭제 로직
    function handleDelete(event, boardId, rowToRemove) {
        event.preventDefault();
        if (confirm("게시글(ID: " + boardId + ")을 삭제하시겠습니까?")) {
            $.ajax({
                type: "DELETE",
                url: "/api/board/" + boardId,
                success: function(response) {
                    if(response === "success") {
                        alert("삭제에 성공했습니다.");
                        boardDetailBox.hide();
                        boardWriteBox.hide();
                        boardListBox.show();
                        rowToRemove.remove();

                        let remainingRows = $('#boardListBox table tbody tr:not([style*="display: none"])').length;

                        if (searchFlag) {
                            let currentPage = searchPageNum;
                            if (remainingRows === 0 && currentPage > 0) {
                                currentPage--;
                            } else if (remainingRows === 0 && currentPage === 0) {
                                $('#boardListBox table tbody').html('<tr class="noDataTr"><td colspan="5">검색 결과가 없습니다.</td></tr>');
                            }
                            handleSearch(initialCondCode, initialInputVal, currentPage);
                        } else {
                            let currentPage = previousListPage;
                            if (remainingRows === 0 && currentPage > 0) {
                                currentPage--;
                            } else if (remainingRows === 0 && currentPage === 0) {
                                $('#boardListBox table tbody').html('<tr class="noDataTr"><td colspan="5">검색 결과가 없습니다.</td></tr>');
                            }
                            loadBoards(currentPage);
                        }
                    } else if (response === "failure_not_found") {
                        alert("존재하지 않는 게시글입니다.");
                    } else if (response === "failure_admin_or_writer") {
                        alert("작성자 혹은 운영자만 삭제할 수 있습니다.");
                    }
                },
                error: function(xhr, status, error) {
                    console.error("AJAX 오류:", status, error, xhr.responseText);
                    alert("[Ajax] 게시글 삭제 중 오류가 발생했습니다.");
                    window.location.href = "/board";
                }
            });
        } else {
            alert("작업을 취소하였습니다.");
        }
    }

    // 현재 페이지 가져오기
    function getCurrentPage() {
        return parseInt($('.pagination .active .page-link').data('page')) || 0;
    }

    // 게시글 검색 - 버튼 클릭
    $(document).on('click', '.searchInput .searchBtn', function(event) {
        event.preventDefault();

        const condCode = $('.condSelect').val();
        let inputVal = $("#inputBox").val();

        if (!condCode) {
            alert('검색조건을 선택하세요.');
            return;
        }

        if(inputVal === "" || inputVal.trim() === "" || !inputVal.trim()) {
            alert("검색어를 입력해주세요.");
            return;
        }

        initialCondCode = condCode;
        initialInputVal = inputVal;
        searchFlag = true;
        searchPageNum = 0;

        handleSearch(condCode, inputVal, searchPageNum)
    });

    // 검색 로직
    function handleSearch(condCode, inputVal, pageNum) {
        $.ajax({
            url: '/board/search',
            type: 'POST',
            data: {
                condCode: condCode,
                inputVal: inputVal,
                page: pageNum
            },
            success: function(response) {
                displayBoardList(response, pageNum);
                $('#searchCancelBtn').show();
            },
            error: function(xhr, status, error) {
                console.error("검색 오류:", status, error, xhr.responseText);
                alert('검색 중 오류가 발생했습니다.');
            }
        });
    }

    // 검색 취소
    $("#searchCancelBtn").click(function() {
        loadBoards(previousListPage);
        $(this).hide();
        $("#inputBox").val('');
        initialCondCode = null;
        initialInputVal = null;
        searchFlag = false;
        searchPageNum = 0;
    });

    // 일반 댓글 저장
    $(document).on('click', '.comment_save_btn', function(e) {
        e.preventDefault();

        let boardId = $('#comment_board_id').val();
        let contentArea = $('#comment_content');
        let loginUser = $('#loginUser').data('login-id');

        let content = contentArea.val();

        if(content === "" || content === null || content.trim() === "") {
            alert("댓글 내용을 작성 후 등록해주세요.");
            contentArea.val("");
            contentArea.focus();
            return;
        }

        $.ajax({
            url: '/board/' + boardId + '/comment/save',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                boardId: boardId,
                content: content,
                writer: loginUser
            }),
            success: function(newComment) {
                if (newComment && newComment.id) {
                    contentArea.val("");

                    // 새로 추가된 댓글을 DOM에 동적으로 추가
                    addCommentToDom(newComment, 0, null);

                    // 댓글 수 업데이트
                    updateCommentCount(1);
                } else {
                    alert("댓글 작성 실패: 유효하지 않은 응답");
                    console.error("Invalid response for comment save:", newComment);
                }
            },
            error: function(xhr, status, error) {
                if (xhr.status === 404) {
                    alert("존재하지 않는 게시글입니다.");
                    loadBoards(0);
                } else if (xhr.status === 403) {
                    alert("댓글 작성 권한이 없습니다.");
                } else {
                    alert("댓글 작성 실패: " + (xhr.responseText || error));
                }
                console.error("댓글 작성 AJAX 오류:", status, error, xhr.responseText);
            }
        });
    })

    // 댓글 버튼 클릭하여 스크롤 이동.
    $(document).on('click', '.comment_btn', function(e) {
        e.preventDefault();

        let targetPosition =  $(".comment-section").offset().top - 30;
        scrollToTarget(targetPosition);
    });

    function scrollToTarget(targetPosition) {
        $("html, body").scrollTop(targetPosition);
    }

    $(document).on('click', '#scrollToTopBtn', function(e) {
        e.preventDefault();
        scrollToTarget(0)
    });

    // 답글 버튼 클릭
    $(document).on('click', '.reply-btn', function() {
        let commentId = $(this).data('comment-id');
        // 부모의 indent level 가져오기.
        let currentIndentLevel = $(this).data('indent-level');
        let replyBtnHtml = $(this);

        let replyFormContainer = $(this).closest('.comment-container').find('.reply-form-container').first();
        let currentBoardId = $('#comment_board_id').val();

        // 취소 상태일 때 클릭 시 버튼 text 답글로 변경 + 입력창 비우기.
        if (replyFormContainer.children().length !== 0) {
            replyFormContainer.empty();
            replyBtnHtml.text("답글");
        }
        else {
            let nextIndentLevel = currentIndentLevel + 1;

            // level 1 까지만 답글 버튼 작동.
            if (nextIndentLevel > 2) {
                return;
            }

            // 입력창 html 생성
            let replyFormHtml = `
                <div class="reply-write-form mt-3 mb-3 ${'indent-level-' + nextIndentLevel}">
                    <input type="hidden" class="reply_board_id" name="boardId" value="${currentBoardId}">
                    <input type="hidden" class="parent_comment_id" name="parentCommentId" value="${commentId}">
                    <div class="reply_input-group input-group">
                        <textarea class="form-control reply_content" name="content" rows="2" placeholder="답글을 입력하세요..." required></textarea>
                        <button class="reply_save_btn btn btn-outline-success">등록</button>
                    </div>
                </div>
            `;
            replyFormContainer.html(replyFormHtml);
            replyBtnHtml.text("취소");
            replyFormContainer.find('.reply_content').focus();
        }
    });

    // 답글 저장
    $(document).on('click', '.reply_save_btn', function(e) {
        e.preventDefault();

        let $replyForm = $(this).closest('.reply-write-form');
        let boardId = $replyForm.find('.reply_board_id').val();
        let parentCommentId = $replyForm.find('.parent_comment_id').val();
        let contentArea = $replyForm.find('.reply_content');
        let loginUser = $('#loginUser').data('login-id');

        let content = contentArea.val();

        if (content === "" || content === null || content.trim() === "") {
            alert('답글 내용을 입력해주세요.');
            contentArea.val("");
            contentArea.focus();
            return;
        }

        $.ajax({
            url: '/board/' + boardId + '/comment/save',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                boardId: boardId,
                parentId: parentCommentId,
                content: content,
                writer: loginUser
            }),
            success: function(newReply) {
                if (newReply && newReply.id) {
                    $replyForm.remove();

                    $(`button.reply-btn[data-comment-id='${parentCommentId}']`).text("답글");

                    // 새로 추가된 답글을 DOM에 동적으로 추가
                    let $parentCommentItem = $(`#comment-item-${parentCommentId}`);
                    let parentIndentLevel = $parentCommentItem.find('.reply-btn').first().data('indent-level');
                    let nextIndentLevel = parentIndentLevel + 1;

                    addCommentToDom(newReply, nextIndentLevel, $parentCommentItem);

                    // 댓글 수 업데이트
                    updateCommentCount(1);
                } else {
                    alert("답글 작성 실패: 유효하지 않은 응답");
                    console.error("Invalid response for reply save:", newReply);
                }
            },
            error: function(xhr, status, error) {
                if (xhr.status === 404) {
                    alert("존재하지 않는 게시글 또는 부모 댓글입니다.");
                } else if (xhr.status === 403) {
                    alert("답글 작성 권한이 없습니다.");
                } else {
                    alert("답글 등록에 실패했습니다: " + (xhr.responseText || error));
                }
                console.error('답글 등록 AJAX 오류:', status, error, xhr.responseText);
            }
        });
    });

    function addCommentToDom(newComment, indentLevel, $parentCommentItem) {

        // 등록 날짜 포맷팅
        let createdAtString = newComment.createdAt;
        let formattedDate = '날짜 없음'; // 기본값

        if (createdAtString) {
            try {
                const date = new Date(createdAtString);
                if (!isNaN(date.getTime())) {
                    const year = date.getFullYear();
                    const month = (date.getMonth() + 1).toString().padStart(2, '0');
                    const day = date.getDate().toString().padStart(2, '0');
                    const hours = date.getHours().toString().padStart(2, '0');
                    const minutes = date.getMinutes().toString().padStart(2, '0');
                    formattedDate = `${year}-${month}-${day} ${hours}:${minutes}`;
                } else {
                    console.warn("Invalid date string received for createdAt:", createdAtString);
                }
            } catch (e) {
                console.error("Error parsing createdAt date:", e, createdAtString);
            }
        } else {
            console.warn("newComment.createdAt is missing or null:", newComment);
        }

        let replyButtonHtml = '';

        // 0, 1 레벨 댓글까지만 답글 버튼 생성
        if (indentLevel < 2) {
            replyButtonHtml = `
            <button type="button" class="btn btn-sm btn-outline-secondary reply-btn"
                    data-comment-id="${newComment.id}" data-indent-level="${indentLevel}">답글</button>
        `;
        }

        // 새로 등록된 댓글의 html 생성. response 받은 데이터로 구성. board detail 양식에 맞춰서 생성. div:commentList > ul:list-unstyled > 안에 내용 생성.
        let newCommentHtml = `
        <li id="li-comment-${newComment.id}">
            <div id="comment-item-${newComment.id}"
                 class="comment-container indent-level-${indentLevel}">
                <p>${newComment.content}</p>
                <div class="comment-meta">
                    <span>${newComment.writer}</span>
                    <span>${formattedDate}</span>
                </div>
                <div class="comment-actions">
                    ${replyButtonHtml} 
                    <button type="button" class="btn btn-sm btn-outline-danger delete-btn"
                            data-comment-id="${newComment.id}" data-board-id="${newComment.boardId}">삭제</button>
                </div>
                <div class="reply-form-container"></div>
                <ul class="list-unstyled mt-2"></ul> 
            </div>
        </li>
        `;

        // 부모 댓글 유무 체크
        if ($parentCommentItem) {
            // 부모 댓글 끝나는 지점 찾아서 붙이기
            let $repliesList = $parentCommentItem.find('> .list-unstyled');
            $repliesList.prepend(newCommentHtml);
        } else {
            // 댓글 리스트에 붙이기
            $('#commentList > .list-unstyled').prepend(newCommentHtml);
            $('#commentList p.text-muted').hide();
        }
    }

    // 댓글 수 업데이트 함수
    function updateCommentCount(change) {
        let $commentCountSpan = $('#commentCount');
        let currentCount = parseInt($commentCountSpan.text()) || 0;
        // 변화값만큼 계산하여 text.
        $commentCountSpan.text(currentCount + change);
    }

//     댓글 삭제 기능
    $(document).on('click', '.comment-actions .delete-btn', function(e) {
        e.preventDefault();
        let commentId = $(this).data('comment-id');
        let boardId = $(this).data('board-id');
        let $commentItemToRemove = $(this).closest('li[id^="li-comment-"]');

        deleteComment(boardId, commentId, $commentItemToRemove);
    });

    function deleteComment(boardId, commentId, $commentItemToRemove) {
        if (confirm("댓글(ID: " + commentId + ")을 삭제하시겠습니까? 이 댓글의 모든 답글도 함께 삭제됩니다.")) {
            $.ajax({
                type: "DELETE",
                url: `/board/${boardId}/comment/${commentId}`,
                success: function(response) {
                    if (typeof response === 'number' && response > 0) {
                        alert("댓글이 성공적으로 삭제되었습니다.");

                        $commentItemToRemove.remove();

                        // 댓글 수 업데이트
                        updateCommentCount(-response);

                        if ($('#commentList > .list-unstyled').children().length === 0) {
                            $('#commentList p.text-muted').show();
                        }
                    } else {
                        alert("댓글 삭제 실패: " + response);
                        console.error("댓글 삭제 실패 응답:", response);
                    }
                },
                error: function(xhr, status, error) {
                    let errorMessage = xhr.responseText || "알 수 없는 오류가 발생했습니다.";
                    if (xhr.status === 403) {
                        alert("댓글 삭제 권한이 없습니다.");
                    } else if (xhr.status === 404) {
                        alert("존재하지 않는 댓글입니다.");
                    } else if (xhr.status === 401) {
                        alert("해당 댓글 삭제 권한이 없습니다.");
                    }
                    else {
                        alert("댓글 삭제 중 오류가 발생했습니다: " + errorMessage);
                    }
                    console.error("댓글 삭제 AJAX 오류:", status, error, xhr.responseText);
                }
            });
        } else {
            alert("삭제 작업을 취소하였습니다.");
        }
    }
});