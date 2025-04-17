$(document).ready(function() {
    checkFormFilled()
    $("#reservationForm input, #reservationForm select, #reservationForm textarea").on("input change", checkFormFilled);


    $("#reservationButton").on("click", function () {
        const teacherName = $('#teacherName').val();
        const teacherPhone = $('#teacherPhone').val();
        const reservationDate = parseKoreanDateTime($('#reservationDate').val())
        const zoomUrl = $('#zoomUrl').val();

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
function checkFormFilled() {
    let allFilled = true;

    $("#reservationForm input, #reservationForm select, #reservationForm textarea").each(function () {
        if (!$(this).val().trim()) {
            allFilled = false;
            return false; // 루프 종료
        }
    });

    $("#reservationButton").prop("disabled", !allFilled);
}
function sendReservation(formData){
    $('#loadingOverlay').show();
    fetch(`/reservation/interview`, {
        method: 'POST',
        body: formData  // Content-Type은 브라우저가 자동으로 multipart/form-data로 설정합니다.
    })
        .then(response => {
            if (response.ok) {
                alert("교사면접 예약완료\n예약이 성공적으로 완료되었습니다.");
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


