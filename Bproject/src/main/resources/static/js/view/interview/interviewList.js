$(document).ready(function() {
    const headers = [
        'No', '선생님 이름', '선생님 연락처','면접일시','줌 주소','접수일', '기타'
    ];
    const sortableColumns = ["매각 기일", "등록일"];

    const jwtToken = getCookie('session-id');
    const payload = parseJWT(jwtToken);
    const userRole = payload.role;
    const id = payload.id;

    getData()
    renderCategoriesToInterview()

    $('#searchDate').on('change', function () {
        const selectedDate = $(this).val();

        searchProducts(selectedDate,"reservationDate")
    });

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
        const formattedCreateDate = formatDateTime(data.createDate);
        const formattedReservationDate = formatDateTime(data.reservationDate);
        const row = $(`<tr id = ${data.id}>`);
        row.append($('<td data-column="index">').text(index + 1));

        row.append($('<td data-column="teacherName">')
            .html('<strong>' + data.teacherName + '</strong>')
            .css('white-space', 'nowrap')
        );
        row.append($('<td data-column="teacherPhone">').text(data.teacherPhone));
        row.append($('<td data-column="reservationDate">')
            .html('<strong>' + formattedReservationDate + '</strong>')
            .css('white-space', 'nowrap')
        );
        row.append($('<td data-column="zoomUrl">').text(data.zoomUrl));
        row.append($('<td data-column="createDate">').text(formattedCreateDate));

        if (userRole == '관리자' || id == data.memberId) {
            row.append($('<td style="display: flex; justify-content: center;" data-column="Actions">').append(deleteButton(data)));
        } else {
            row.append($('<td data-column="Actions">').text(""));
        }
        return row;
    }

    function deleteButton(data) {
        return $('<button>').text('취소').addClass('btn btn-danger mx-lg-1 btn-sm').on('click', function () {
            if (confirm(`예약을 취소할까요?\n\n확인 시 예약 취소 연락이 전달되며\n재예약시 처음부터 다시시도 해야합니다.`)) {
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
                    alert("예약이 성공적으로 취소되었습니다.")
                    getData()
                } else {
                    alert("예약 취소를 실패하였습니다.")
                }
            })
            .catch(error => {
                console.log(error)
            });
    }
})


