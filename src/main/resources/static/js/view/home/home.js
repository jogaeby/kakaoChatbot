$(document).ready(function() {
    var calendarEl = document.getElementById('calendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
        expandRows: true,
        initialView: 'dayGridMonth',
        locale: 'ko', // 한국어로 설정
        timeZone: 'Asia/Seoul',
        headerToolbar: {
            left: 'prev',
            right: 'today,next',
            center: 'title',
        },
        buttonText: {
          today : "현재날짜"
        },
        events: [

        ],
        datesSet: function(info) {
            getDateRange(info.start, info.end); // 뷰가 변경될 때 날짜 범위 구하는 함수 호출
        },
        dateClick: function(info) {
            getWorkTime(info.dateStr)
            // showModal(info.dateStr,time);
        },
        eventClick: function(info) {
            // 클릭한 이벤트의 날짜로 이동
            const eventDate = formatDate(info.event.start); // 이벤트 시작 날짜
            let status =  info.event.extendedProps.status

            if (status != "근무시작시간") {
                const url = `/schedule?date=${eventDate}&status=${status}`; // 이벤트 날짜로 링크 설정
                window.location.href = url; // 링크로 이동
            }

            if (status == "근무시작시간") {
                getWorkTime(eventDate)
            }
        },
        eventMouseEnter: function(info) {
            // 이벤트에 호버 효과 적용
            const originalBackgroundColor = $(info.el).css('background-color');

            // 기존 색상을 기반으로 약간 어두운 색상으로 변경
            const darkerColor = darkenColor(originalBackgroundColor, 0.1); // 10% 어둡게
            $(info.el).css('background-color', darkerColor);
        },
        eventMouseLeave: function(info) {
            // 이벤트 호버 해제
            $(info.el).css('background-color', info.event.backgroundColor || '');
        },
    });

    calendar.render();
    // 주기적으로 날짜 범위를 가져오는 폴링 기능 (예: 5분마다 폴링)
    setInterval(function() {
        var view = calendar.view; // 현재 뷰 정보를 가져옴
        getDateRange(view.activeStart, view.activeEnd); // 뷰의 시작, 종료 날짜로 폴링
    }, 1 * 60 * 1000); // 5분 (밀리초 단위: 1분 = 1 * 60 * 1000)

    $('.fc-daygrid-day-number').css('text-decoration', 'none');

    function formatDate(date) {
        return date.toISOString().split('T')[0]; // YYYY-MM-DD 형식으로 변환
    }

    function darkenColor(color, percent) {
        const rgb = color.match(/\d+/g); // RGB 값 추출
        const r = Math.floor(rgb[0] * (1 - percent));
        const g = Math.floor(rgb[1] * (1 - percent));
        const b = Math.floor(rgb[2] * (1 - percent));
        return `rgb(${r},${g},${b})`;
    }

    // 뷰의 날짜 범위를 구하는 함수
    function getDateRange(start, end) {
        end.setDate(end.getDate() - 1); // end 날짜에서 하루를 뺌

        start = formatDate(start)
        end = formatDate(end)

        // AJAX 통신으로 서버에서 이벤트 데이터를 가져오기
        $.ajax({
            url: `/schedule/calendar?startDate=${start}&endDate=${end}`, // 서버의 이벤트 API 엔드포인트
            method: 'GET',
            success: function(data) {
                calendar.removeAllEvents(); // 기존 이벤트 삭제
                calendar.addEventSource(data); // 새로운 이벤트 추가
            },
            error: function(xhr, status, error) {
                console.error("이벤트 데이터를 가져오는 데 오류가 발생했습니다:", error);
            }
        });
    }

    function showModal(date, time, id) {
        $('#addWorkTimeId').val(id); // ���무시작시간 설정
        $('#addModalLabel').text(date);
        $('#addWorkTime').val(time); // ���무시작시간 설정
        $('#addModal').modal('show');
    }

    $("#addWorkTimeButton").on("click", function () {
        const date = $('#addModalLabel').text();
        const time = $('#addWorkTime').val();
        const id = $('#addWorkTimeId').val();

        if (confirm(date+ " 날짜의 근무시작 시간을 " + time + " 설정하시겠습니까?")){
            if (id){
                $.ajax({
                    url: `/schedule/works`, // 서버의 이벤트 API 엔드포인트
                    method: 'PATCH',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        id: id,
                        date: date,
                        time: time
                    }),
                    success: function(data) {
                        alert("성공적으로 설정하였습니다.")
                        $('#addModal').modal('hide');

                        var view = calendar.view; // 현재 뷰 정보를 가져옴
                        getDateRange(view.activeStart, view.activeEnd); // 뷰의 시작, 종료 날짜로 폴링
                    },
                    error: function(xhr, status, error) {
                        alert("설정을 실패하였습니다.")
                        console.error("이벤트 데이터를 가져오는 데 오류가 발생했습니다:", error);
                    }
                });
            }else {
                $.ajax({
                    url: `/schedule/works`, // 서버의 이벤트 API 엔드포인트
                    method: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        id: id,
                        date: date,
                        time: time
                    }),
                    success: function(data) {
                        alert("성공적으로 설정하였습니다.")
                        $('#addModal').modal('hide');

                        var view = calendar.view; // 현재 뷰 정보를 가져옴
                        getDateRange(view.activeStart, view.activeEnd); // 뷰의 시작, 종료 날짜로 폴링
                    },
                    error: function(xhr, status, error) {
                        alert("설정을 실패하였습니다.")
                        console.error("이벤트 데이터를 가져오는 데 오류가 발생했습니다:", error);
                    }
                });
            }

        }

    })

    function getWorkTime(date) {
        $.ajax({
            url: `/schedule/works?date=${date}`, // 서버의 이���트 API ��드포인트
            method: 'GET',
            success: function(data) {
                if(data == null) {
                    showModal(date,"","")
                }else {
                    showModal(date,data.time,data.id)
                }
            },
            error: function(xhr, status, error) {
                console.error("데이터를 가져오는 중에 오류가 발생했습니다:", error);
            }
        });
    }
});
