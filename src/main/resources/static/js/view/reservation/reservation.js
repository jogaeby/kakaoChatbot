$(document).ready(function() {
    $("#reservationButton").on("click", function () {
        const studentName = $('#studentName').val();
        const studentPhone = $('#studentPhone').val();
        const teacherName = $('#teacherName').val();
        const teacherPhone = $('#teacherPhone').val();
        const reservationDate = $('#reservationDate').val();
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

    $("#studentInfo").on("input", function() {
        let textarea = $(this)[0]; // DOM 요소 가져오기
        let value = textarea.value; // 현재 입력된 값
        let cursorPos = textarea.selectionStart; // 현재 커서 위치

        // 특정 조건을 만족할 때 커서를 처음으로 이동
        if (value === "") {
            textarea.setSelectionRange(0, 0);
        }

        // 예: 특정 문자열 입력 시 커서 이동 (줄 바꿈 후 자동 들여쓰기 예제)
        if (value[cursorPos - 1] === "\n") {
            let spaces = "    "; // 4칸 스페이스 들여쓰기
            let newValue = value.substring(0, cursorPos) + spaces + value.substring(cursorPos);
            $(this).val(newValue);
            textarea.setSelectionRange(cursorPos + spaces.length, cursorPos + spaces.length);
        }
    });

})

function sendReservation(formData){
    fetch(`/reservation/trial`, {
        method: 'POST',
        body: formData  // Content-Type은 브라우저가 자동으로 multipart/form-data로 설정합니다.
    })
        .then(response => {
            if (response.ok) {
                alert("성공적으로 예약하였습니다.");
                $('#studentName').val("");
                $('#studentPhone').val("");
                $('#teacherName').val("");
                $('#teacherPhone').val("");
                $('#reservationDate').val("");
                $('#studentInfo').val("");
            } else {
                alert("예약을 실패하였습니다.");
            }
        })
        .catch(error => {
            console.log(error);
        });
}


