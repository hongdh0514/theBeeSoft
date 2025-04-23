$(document).ready(function() {
    console.log("jQuery 로드 완료, DOM 준비됨");

    let boardListBox = $("#boardListBox");
    let boardDetailBox = $("#boardDetailBox");
    let boardWriteBox = $("#boardWriteBox");
    let boardResultBox = $("#boardResultBox");

    // 에러 메시지 알럿
    const errorMessage = $('body').data('error-message');
    if (typeof errorMessage === 'string' && errorMessage.trim() !== '') {
        alert(errorMessage);
    }

    // 게시글 작성
    $(".newBtn").click(function(e) {

        $.ajax({
            type: "GET",
            url: "/board/new_t",
            success: function(fragment) {
                boardListBox.hide();
                boardDetailBox.hide();
                boardResultBox.hide();
                boardWriteBox.html(fragment).show();
            }
        });
    })

    // 게시글 상세
    $(document).on('click', '#boardListBox .titleBtn', function(e) {
        e.preventDefault();
        let boardId = $(this).data('board-id');
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
                            boardResultBox.hide();
                            boardWriteBox.hide();
                            boardDetailBox.html(fragment).show();
                        }
                    });
                }
                else if (response === "failure_not_found") {
                    alert("[Ajax] 존재하지 않는 게시글입니다.")
                }
                else if (response === "failure_admin_or_writer") {
                    alert("[Ajax] 작성자 혹은 운영자만 조회할 수 있습니다.")
                }
            },
            error: function(xhr, status, error) {
                console.error("AJAX 오류:", status, error, xhr.responseText);
                alert("[Ajax] 게시글 조회 중 오류가 발생했습니다.");
                window.location.href = "/board";
            }
        });
    });

    // 게시글 삭제 - 목록
    $(document).on('click', '#boardListBox .deleteBtn', function(event) {
        handleDelete(event, $(this).data('board-id'), $(this).closest('tr'));
    });

    // 게시글 삭제 - 상세
    $(document).on('click', '#boardDetailBox .deleteBtn', function(event) {
        handleDelete(event, $(this).data('board-id'), $("#boardListBox").find('a[data-board-id="' + $(this).data('board-id') + '"]').closest('tr'));
    });

    function handleDelete(event, boardId, rowToRemove) {
        event.preventDefault();
        if (confirm("게시글(ID: " + boardId + ")을 삭제하시겠습니까?")) {
            $.ajax({
                type: "DELETE",
                url: "/api/board/" + boardId,
                success: function(response) {
                    if(response === "success") {
                        alert("[Ajax] 삭제에 성공했습니다.");
                        boardDetailBox.hide();
                        boardResultBox.hide();
                        boardWriteBox.hide();
                        boardListBox.show();
                        rowToRemove.fadeOut('slow', function() {
                            rowToRemove.remove();
                            if ($('#boardListBox table tbody tr').length === 0) {
                                $('#boardListBox table tbody').html('<tr class="noDataTr"><td colspan="5">검색 결과가 없습니다.</td></tr>');
                            }
                        });
                    }
                    else if (response === "failure_not_found") {
                        alert("[Ajax] 존재하지 않는 게시글입니다.");
                    }
                    else if (response === "failure_admin_or_writer") {
                        alert("[Ajax] 작성자 혹은 운영자만 삭제할 수 있습니다.");
                    }
                },
                error: function(xhr, status, error) {
                    console.error("AJAX 오류:", status, error, xhr.responseText);
                    alert("[Ajax] 게시글 삭제 중 오류가 발생했습니다.");
                    window.location.href = "/board";
                }
            });
        }
        else {
            alert("작업을 취소하였습니다.");
        }
    }

    // 목록으로
    $(document).on('click', '.backToList', function(e) {
        e.preventDefault();
        boardDetailBox.hide();
        boardWriteBox.hide();
        boardResultBox.hide();
        boardListBox.show();
    });

    $(".searchBtn").click(function(e) {
        e.preventDefault();

        const condCode = $('.condSelect').val();
        let inputVal = $("#inputBox").val();

        if (!condCode) {
            alert('검색조건을 선택하세요.');
            return 0;
        }

        if(inputVal === "" || inputVal.trim() === "" || !inputVal.trim()) {
            alert("검색어를 입력해주세요.")
            return 1;
        }

        $.ajax({
            url: '/board/search',
            type: 'POST',
            data: {
                condCode: condCode,
                inputVal: inputVal
            },
            success: function(response) {
                // 서버에서 반환된 HTML 조각으로 게시글 목록 영역 업데이트
                $('#boardListBox table tbody').html(response);
                $('#searchCancelBtn').show();
            },
            error: function() {
                alert('검색 중 오류가 발생했습니다.');
            }
        });
    })

    $("#searchCancelBtn").click(function() {
        loadAllBoards(); // 전체 게시글 목록 다시 불러오기
        $(this).hide(); // 취소 버튼 다시 숨기기
        $("#inputBox").val(''); // 검색어 입력 필드 초기화 (선택 사항)
    });

    function loadAllBoards() {
        $.ajax({
            url: '/board/boardAll', // 전체 게시글 목록 API 엔드포인트 (이전 답변 참고)
            type: 'GET',
            dataType: 'html', // 서버에서 HTML 조각을 받아서 처리
            success: function(response) {
                $('#boardListBox table tbody').html(response);
            },
            error: function() {
                alert('전체 게시글 목록을 불러오는 중 오류가 발생했습니다.');
            }
        });
    }
});
