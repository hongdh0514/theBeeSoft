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

                        // let currentPage = getCurrentPage();
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

    // 일반 댓글 작성
    $(document).on('click', '.comment_save_btn', function(e) {
        e.preventDefault();

        let boardId = $('#comment_board_id').val()
        let contentArea = $('#comment_content')
        let loginUser = $('#loginUser').data('login-id')

        let content = contentArea.val();

        if(content === "" || content === null || content.trim() === "") {
            alert("댓글 내용 작성 후 등록해주세요;")
            contentArea.focus()
            return;
        }

        $.ajax({
            url: '/board/' + boardId + '/comment/save',
            type: 'POST',
            data: {
                boardId: boardId,
                content: content,
                loginUser: loginUser
            },
            success: function(response) {
                if (response === "success") {
                    // alert("댓글 작성 성공" + response);
                    contentArea.val('');
                    loadBoardDetail(boardId);
                }
                else if (response === "no_board_found") {
                    alert("존재하지 않는 게시글입니다.");
                    loadBoards(0); // 목록으로 이동
                }
            },
            error: function(xhr, status, error) {
                alert("댓글 작성 실패");
                loadBoardDetail(boardId);
            }
        });
    });



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

//     답글
    $(document).on('click', '.reply-btn', function() {
        let commentId = $(this).data('comment-id');
        let replyBtnHtml = $(this);
        let replyFormContainer = $(this).closest('li').find('.reply-form-container');
        let currentBoardId = $('#comment_board_id').val();


        if (replyFormContainer.children().length > 0) {
            replyFormContainer.empty();
            replyBtnHtml.html("답글");
        } else {
            let replyFormHtml = `
                    <div class="reply-write-form mt-3 mb-3" style="margin-left: 20px;">
                        <input type="hidden" class="reply_board_id" name="boardId" value="${currentBoardId}">
                        <input type="hidden" class="parent_comment_id" name="parentCommentId" value="${commentId}">
                        <div class="reply_input-group input-group">
                            <textarea class="form-control reply_content" name="content" rows="2" placeholder="답글을 입력하세요..." required></textarea>
                            <button class="reply_save_btn btn btn-outline-success">등록</button>
                        </div>
                    </div>
                `;
            replyFormContainer.html(replyFormHtml);
            replyBtnHtml.html("취소");
        }
    });
});