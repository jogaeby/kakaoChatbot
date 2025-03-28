$(document).ready(function() {
    $("#reservationButton").on("click", function () {
        const teacherName = $('#teacherName').val();
        const teacherPhone = $('#teacherPhone').val();
        const reservationDate = parseKoreanDateTime($('#reservationDate').val())
        const zoomUrl = $('#zoomUrl').val();
        console.log(reservationDate)

        if (!teacherName) {
            alert("선생님 이름을 입력해주세요.")
            return
        }
        if (!teacherPhone) {
            alert("선생님 연락처를 입력해주세요.")
            return
        }

        if (!reservationDate) {
            alert("면접 날짜를 선택해주세요.")
            return
        }

        if (!zoomUrl) {
            alert("줌 주소를 선택해주세요.")
            return
        }

        const formData = new FormData();
        formData.append('teacherName', teacherName);
        formData.append('teacherPhone', teacherPhone);
        formData.append('reservationDate', reservationDate);
        formData.append('zoomUrl', zoomUrl);

        sendReservation(formData)
    })

})

function sendReservation(formData){
    $('#loadingOverlay').show();
    fetch(`/reservation/interview`, {
        method: 'POST',
        body: formData  // Content-Type은 브라우저가 자동으로 multipart/form-data로 설정합니다.
    })
        .then(response => {
            if (response.ok) {
                alert("예약이 성공적으로 접수되었습니다.");
                $('#teacherName').val("");
                $('#teacherPhone').val("");
                $('#reservationDate').val("");
                $('#zoomUrl').val("");

            } else {
                alert("예약을 실패하였습니다.");
            }
            $('#loadingOverlay').hide();
        })
        .catch(error => {
            console.log(error);
        });
}


