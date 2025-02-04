$(document).ready(function(){
    const headers = [
        'No', '이름', '연락처','근무일자','참여 여부','신청일','상태','관리'
    ];
    // 현재 날짜 가져오기
    const currentDate = new Date();
    const year = currentDate.getFullYear();
    const month = String(currentDate.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1, 두 자리로 맞춤
    const day = String(currentDate.getDate()).padStart(2, '0'); // 두 자리로 맞춤
    const formattedDate = `${year}-${month}-${day}`;

    // 현재 URL의 쿼리 파라미터 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const reservationDateParam = urlParams.get('date'); // 'reservationDate' 파라미터 값 가져오기
    const status = urlParams.get('status');

    // 예약 날짜가 쿼리 파라미터에 있으면 해당 값으로 설정하고, 없으면 현재 날짜로 설정
    const searchReservationDate = reservationDateParam ? reservationDateParam : formattedDate;
    $('#reservationDate').val(searchReservationDate); // 예약 날짜를 입력 필드에 설정

    let reservationDate = $('#reservationDate').val();

    getSchedule(reservationDate)

    // 날짜가 변경될 때 실행될 함수
    $('#reservationDate').change(function() {
        const selectedDate = $(this).val();


        // URL의 쿼리 파라미터 변경
        const currentUrl = new URL(window.location.href);
        currentUrl.searchParams.set('date', selectedDate); // reservationDate 쿼리 파라미터를 선택한 날짜로 설정
        window.history.pushState({}, '', currentUrl); // URL 업데이트

        if (selectedDate == "") {
            return
        }
        // 원하는 추가 작업을 이곳에 추가
        getSchedule(selectedDate);
    });

    function getSchedule(date) {
        fetch(`/schedule/list?date=${date}&status=${status}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
        })
            .then(response => {
                return response.json()
            }).then(data => {
            renderTable(data,headers,createTableRow)
        })
            .catch(error => {
                console.log(error)
            });
    }

    function createTableRow(data, index) {
        const row = $('<tr>');
        row.append($('<td>').text(index + 1));
        row.append($('<td>').text(data.name));
        row.append($('<td>').text(data.phone));
        row.append($('<td>').text(data.reservationDate));
        row.append($('<td>').text(data.isJoin));
        row.append($('<td>').text(data.createDate));
        row.append($('<td>').text(data.status));

        if (data.status =='신청') {
            row.append($('<td style="display: flex; justify-content: center;">').append(admissionButton(data)).append(cancelButton(data)));
            return row;
        }

        if (data.status =='승인') {
            if (data.isJoin == '참여') {
                row.append($('<td style="display: flex; justify-content: center;">').append(updateIsJoinButton(data)).append(completeButton(data)).append(cancelButton(data)));
                return row;
            }
            if (data.isJoin == '미참여') {
                row.append($('<td style="display: flex; justify-content: center;">').append(updateIsJoinButton(data)).append(cancelButton(data)));
                return row;
            }

        }

        if (data.status =='완료' ||data.status =='취소') {
            row.append($('<td>').text(""));
            return row;
        }

        row.append($('<td>').text(""));
        return row;
    }

    function updateIsJoinButton (data) {
        let buttonName = "";
        let buttonColor = "";
        if (data.isJoin === "미참여") {
            buttonName = "참여"
            buttonColor = "success"
        }

        if (data.isJoin === "참여") {
            buttonName = "미참여"
            buttonColor = "dark"
        }

        return  $('<button>').text(buttonName).addClass(`btn btn-${buttonColor} mx-lg-1 btn-sm`).on('click',function (){

            if (confirm(`${data.name}님의 ${data.reservationDate} 근무를 ${buttonName} 하시겠습니까?`)) {
                updateReservationIsJoin(data.id)
            }

        })
    }

    function admissionButton (data) {
        return  $('<button>').text('승인').addClass('btn btn-info mx-lg-1 btn-sm').on('click',function (){
            $('#admissionModal').find('input').val('');
            $("#admissionModal").modal('show');

            $('#memberName').text(data.name)
            $('#memberPhone').text(data.phone)
            $('#memberGender').text(data.gender)
            $('#memberBirthDate').text(data.birthDate)
            $('#memberAddress').text(data.address)
            $('#workDate').text(data.reservationDate)
            // if (confirm(`${data.name}님의 ${data.reservationDate} 근무를 승인하시겠습니까?`)) {
            //     updateReservationStatus(data.id,"승인")
            // }
            $('#reservationAdmission').off('click').on('click', function () {
                if (confirm(`${data.name}님의 ${data.reservationDate} 근무를 승인하시겠습니까?`)) {
                    // const teamName = $('#teamName').val();
                    // const workTime = $('#workTime').val();
                    // const bus = $('#bus').val()
                    // const busZone = $('#busZone').val()
                    // const busDepartureTime = $('#busDepartureTime').val()
                    //
                    // if (!teamName) {
                    //     alert('소속을 입력하세요.')
                    //     return
                    // }
                    //
                    // if (!workTime) {
                    //     alert('근무시간을 입력하세요.')
                    //     return
                    // }
                    //
                    // if (!bus) {
                    //     alert('버스번호를 입력하세요.')
                    //     return
                    // }
                    //
                    // if (!busZone) {
                    //     alert('버스 탑승지를 입력하세요.')
                    //     return
                    // }
                    //
                    // if (!busDepartureTime) {
                    //     alert('버스 출발시간을 입력하세요.')
                    //     return
                    // }

                    updateReservationStatus(data.id,"승인")
                }
            });
        })
    }

    function completeButton (data) {
        return  $('<button>').text('완료').addClass('btn btn-primary mx-lg-1 btn-sm').on('click',function (){

            if (confirm(`${data.name}님의 ${data.reservationDate} 근무를 완료하시겠습니까?`)) {
                updateReservationStatus(data.id,"완료")
            }
        })
    }

    function cancelButton (data) {
        return  $('<button>').text('취소').addClass('btn btn-danger mx-lg-1 btn-sm').on('click',function (){

            if (confirm(`${data.name}님의 ${data.reservationDate} 근무를 취소하시겠습니까?`)) {
                updateReservationStatus(data.id,"취소")
            }
        })
    }

    function updateReservationIsJoin(id) {
        fetch(`/schedule/isJoin/${id}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
        })
            .then(response => {
                if (response.ok) {
                    alert("성공적으로 수정하였습니다.")
                    getSchedule(reservationDate)
                }else {
                    alert("수정을 실패하였습니다.")
                }

            })
            .catch(error => {
                console.error(error)
            });
    }

    function updateReservationStatus(id,status) {
        fetch(`/schedule/status/${id}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                status: status,

            })
        })
            .then(response => {
                if (response.ok) {
                    alert("성공적으로 변경하였습니다.")
                    getSchedule(reservationDate)
                    $("#admissionModal").modal('hide');
                }else {
                    alert("변경을 실패하였습니다.")
                }

            })
            .catch(error => {
                console.error(error)
            });
    }

})


