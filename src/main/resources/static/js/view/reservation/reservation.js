$(document).ready(function() {
    checkFormFilled()
    $("#reservationForm input, #reservationForm select, #reservationForm textarea").on("input change", checkFormFilled);

    $("#reservationButton").on("click", function () {
        const studentName = $('#studentName').val();
        const studentPhone = $('#studentPhone').val();
        const teacherName = $('#teacherName').val();
        const teacherPhone = $('#teacherPhone').val();
        const reservationDate = parseKoreanDateTime($('#reservationDate').val())
        const studentInfo = $('#studentInfo').val();

        if (!studentName) {
            alert("수강생 이름을 입력해주세요.")
            return
        }

        if (!studentPhone) {
            alert("수강생 연락처를 입력해주세요.")
            return
        }

        if (!teacherName) {
            alert("선생님 이름을 입력해주세요.")
            return
        }
        if (!teacherPhone) {
            alert("선생님 연락처를 입력해주세요.")
            return
        }

        if (!reservationDate) {
            alert("체험 날짜를 선택해주세요.")
            return
        }
        const formData = new FormData();
        formData.append('studentName', studentName);
        formData.append('studentPhone', studentPhone);
        formData.append('teacherName', teacherName);
        formData.append('teacherPhone', teacherPhone);
        formData.append('reservationDate', reservationDate);
        formData.append('studentInfo', studentInfo);

        sendReservation(formData)
    })

    $("#studentInfo").on("focus", function () {
        setTimeout(() => {
            if ($this.val().length === 0) {

                $this[0].setSelectionRange(0, 0); // 텍스트가 없으면 맨 앞으로 이동
            }
        }, 0);
    });

})
function checkFormFilled() {
    let allFilled = true;

    $("#reservationForm input, #reservationForm select, #reservationForm textarea").each(function () {
        console.log(this)
        if (!$(this).val().trim()) {
            allFilled = false;
            return false; // 루프 종료
        }
    });

    $("#reservationButton").prop("disabled", !allFilled);
}

function sendReservation(formData){
    $('#loadingOverlay').show();
    fetch(`/reservation/trial`, {
        method: 'POST',
        body: formData  // Content-Type은 브라우저가 자동으로 multipart/form-data로 설정합니다.
    })
        .then(response => {
            if (response.ok) {
                alert("체험레슨 예약완료\n예약이 성공적으로 완료되었습니다.");
                $('#studentName').val("");
                $('#studentPhone').val("");
                $('#teacherName').val("");
                $('#teacherPhone').val("");
                $('#reservationDate').val("");
                $('#studentInfo').val("");
            } else {
                alert("예약을 실패하였습니다.");
            }
            $('#loadingOverlay').hide();
        })
        .catch(error => {
            console.log(error);
        });
}


