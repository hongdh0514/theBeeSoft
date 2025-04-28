let initialCondCode;
let initialInputVal;

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

    // 게시글 조회
    function loadBoards(page) {
        $.ajax({
            url: '/board/boardAll',
            type: 'GET',
            data: { page: page },
            dataType: 'html',
            success: function(response) {
                $('#boardListBox table tbody').html(response);
                let totalPages = $(response).find('#totalPages').data('total-pages') || 1;
                updatePagination(totalPages, page);
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

        // 페이지 범위 설정 (최대 10개 페이지 표시)
        let startPage = Math.max(0, currentPage - 5); // 현재 페이지 기준 좌우 5개
        let endPage = Math.min(totalPages, startPage + 10); // 최대 10개 페이지 표시

        // "이전" 버튼
        if (currentPage > 0) {
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" data-page="${currentPage - 1}">이전</a></li>`;
        } else {
            paginationHtml += `<li class="page-item disabled"><span class="page-link">이전</span></li>`;
        }

        // 페이지 번호 버튼
        for (let i = startPage; i < endPage; i++) {
            paginationHtml += `<li class="page-item ${i === currentPage ? 'active' : ''}">
                <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
            </li>`;
        }

        // "다음" 버튼
        if (currentPage < totalPages - 1) {
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" data-page="${currentPage + 1}">다음</a></li>`;
        } else {
            paginationHtml += `<li class="page-item disabled"><span class="page-link">다음</span></li>`;
        }

        paginationHtml += '</ul></nav>';
        $('#boardListBox .pagination-container').html(paginationHtml);
    }

    // 페이지 버튼 클릭
    $(document).on('click', '.page-link', function(e) {
        e.preventDefault();
        let page = $(this).data('page');
        console.log("페이지 이동 요청: page =", page);
        if ($('#searchCancelBtn').is(':visible')) {
            let condCode = $('.condSelect').val();
            let inputVal = $("#inputBox").val();
            $.ajax({
                url: '/board/search',
                type: 'POST',
                data: {
                    condCode: condCode,
                    inputVal: inputVal,
                    page: page
                },
                success: function(response) {
                    $('#boardListBox table tbody').html(response);
                    let totalPages = $(response).find('#totalPages').data('total-pages') || 1;
                    console.log("검색 페이지 이동 후 totalPages:", totalPages);
                    updatePagination(totalPages, page);
                },
                error: function() {
                    alert('검색 중 오류가 발생했습니다.');
                }
            });
        } else {
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
                            boardWriteBox.hide();
                            boardDetailBox.html(fragment).show();
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
    });

    // 목록으로
    $(document).on('click', '.backToList', function(e) {
        e.preventDefault();
        boardDetailBox.hide();
        boardWriteBox.hide();
        boardListBox.show();
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
                        // if ($('#searchCancelBtn').is(':visible')) {
                        //     alert("검색 중에는 게시글을 삭제할 수 없습니다.")
                        //     return 0;
                        // }
                        alert("삭제에 성공했습니다.");
                        boardDetailBox.hide();
                        boardWriteBox.hide();
                        boardListBox.show();
                        rowToRemove.remove();

                        let currentPage = getCurrentPage();
                        let remainingRows = $('#boardListBox table tbody tr:not([style*="display: none"])').length;

                        // 검색 중
                        if ($('#searchCancelBtn').is(':visible')) {

                            // 현재 페이지에 남은 게시글이 없고, 현재 페이지가 0이 아니면 이전 페이지로 이동
                            if (remainingRows === 0 && currentPage > 0) {
                                currentPage--;
                            } else if (remainingRows === 0 && currentPage === 0) {
                                // 첫 페이지가 비어 있으면 빈 상태 유지
                                $('#boardListBox table tbody').html('<tr class="noDataTr"><td colspan="5">검색 결과가 없습니다.</td></tr>');
                            }
                            // 삭제된 후 게시글 목록 검색
                            handleSearch(initialCondCode, initialInputVal, currentPage);
                        } else {
                            // 현재 페이지에 남은 게시글이 없고, 현재 페이지가 0이 아니면 이전 페이지로 이동
                            if (remainingRows === 0 && currentPage > 0) {
                                currentPage--;
                            } else if (remainingRows === 0 && currentPage === 0) {
                                // 첫 페이지가 비어 있으면 빈 상태 유지
                                $('#boardListBox table tbody').html('<tr class="noDataTr"><td colspan="5">검색 결과가 없습니다.</td></tr>');
                            }
                            // 남은 게시글이 있으면 페이지 유지
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

        // 처음 검색 조건 저장
        initialCondCode = condCode;
        initialInputVal = inputVal;

        handleSearch(condCode, inputVal, 0)
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
                $('#boardListBox table tbody').html(response);
                $('#searchCancelBtn').show();
                let totalPages = $(response).find('#totalPages').data('total-pages') || 1;
                console.log("검색 후 totalPages:", totalPages);
                updatePagination(totalPages, pageNum);
            },
            error: function(xhr, status, error) {
                console.error("검색 오류:", status, error, xhr.responseText);
                alert('검색 중 오류가 발생했습니다.');
            }
        });
    }

    // 검색 취소
    $("#searchCancelBtn").click(function() {
        loadBoards(0);
        $(this).hide();
        $("#inputBox").val('');
        initialCondCode = null;
        initialInputVal = null;
    });
});