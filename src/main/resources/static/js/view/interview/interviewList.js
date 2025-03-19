$(document).ready(function() {
    const headers = [
        'No', '선생님 이름', '선생님 연락처','면접일시','접수일', '기타'
    ];
    const sortableColumns = ["매각 기일", "등록일"];

    const jwtToken = getCookie('session-id');
    const payload = parseJWT(jwtToken);
    const userRole = payload.role;
    const id = payload.id;

    getData()
    renderCategoriesToInterview()

    $("#searchButton").on("click", function () {
        const searchInput = $('#searchInput').val();
        const category = $('#categorySelect').val();

        if (category && !searchInput) {
            alert("검색어를 입력하세요.")
            return
        }
        if (!category && searchInput) {
            alert("카테고리를 선택하세요.")
            return
        }

        if (!category && !searchInput) {
            getData()
            return;
        }

        searchProducts(searchInput, category)
    })

    function searchProducts(searchInput, searchCategory) {
        fetch(`/reservation/search?input=${searchInput}&category=${searchCategory}&type=인터뷰`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                return response.json()
            }).then(data => {
            renderTable(data, headers, createTableRow, sortableColumns)
        })
            .catch(error => {
                console.log(error)
            });
    }

    function getData() {
        const page = 0
        const size = 20
        const sort = 'reservationDate,desc'

        fetch(`/reservation/interview/list?page=${page}&size=${size}&sort=${sort}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                return response.json()
            }).then(data => {
            renderTable(data, headers, createTableRow, sortableColumns)
        })
            .catch(error => {
                console.log(error)
            });
    }

    function createTableRow(data, index) {
        const formattedCreateDate = formatDate(data.createDate);
        const formattedReservationDate = formatDate(data.reservationDate);
        const row = $(`<tr id = ${data.id}>`);
        row.append($('<td data-column="Index">').text(index + 1));

        row.append($('<td data-column="Status">').text(data.teacherName));
        row.append($('<td data-column="Status">').text(data.teacherPhone));
        row.append($('<td data-column="Status">').text(formattedReservationDate));
        // 날짜 포맷 변경 (YYYY-MM-DD)

        row.append($('<td data-column="등록일">').text(formattedCreateDate));

        if (userRole == '관리자' || id == data.memberId) {
            row.append($('<td style="display: flex; justify-content: center;" data-column="Actions">').append(deleteButton(data)));
        } else {
            row.append($('<td data-column="Actions">').text(""));
        }
        return row;
    }

    function deleteButton(data) {
        return $('<button>').text('삭제').addClass('btn btn-danger mx-lg-1 btn-sm').on('click', function () {
            if (confirm(`[${data.teacherName}]의 [${data.reservationDate}] 예약을 삭제하시겠습니까?`)) {
                deleteProduct(data.id)
            }
        })
    }

    function deleteProduct(id) {
        fetch(`/reservation/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (response.ok) {
                    alert("성공적으로 삭제하였습니다.")
                    getData()
                } else {
                    alert("삭제를 실패하였습니다.")
                }
            })
            .catch(error => {
                console.log(error)
            });
    }
})


