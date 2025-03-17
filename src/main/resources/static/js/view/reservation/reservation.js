$(document).ready(function() {
    $("#reservationButton").on("click", function () {
        const studentName = $('#studentName').val();
        const studentPhone = $('#studentPhone').val();
        const teacherName = $('#teacherName').val();
        const teacherPhone = $('#teacherPhone').val();
        const reservationDate = $('#reservationDate').val();
        const studentInfo = $('#studentInfo').val();

        console.log(studentName,studentPhone,teacherName,teacherPhone,reservationDate,studentInfo)

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

})

function sendReservation(formData){
    fetch(`/reservation`, {
        method: 'POST',
        body: formData  // Content-Type은 브라우저가 자동으로 multipart/form-data로 설정합니다.
    })
        .then(response => {
            if (response.ok) {
                alert("성공적으로 추가하였습니다.");
                $('#addModal').modal('hide');
            } else {
                alert("추가를 실패하였습니다.");
            }
        })
        .catch(error => {
            console.log(error);
        });
}


