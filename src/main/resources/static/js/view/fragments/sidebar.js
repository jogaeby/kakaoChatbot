$(document).ready(function(){
    const sidebar = document.getElementById('sidebar'); // 사이드바 요소 가져오기
    let jwtToken = getCookie('session-id');

    if (jwtToken) {
        const payload = parseJWT(jwtToken);
        let userRole = payload.role;
        // 권한에 따라 보여줄 메뉴를 설정합니다.
        // 권한에 따른 사이드바 메뉴 구성

        // 모든 메뉴를 담을 HTML 문자열 초기화
        let menuHtml = '';

        if (userRole != '관리자') {
            menuHtml += `
                 <div class="nav">
                    <a class="nav-link" href="/reservation" >
                        <div class="sb-nav-link-icon"><i class='far fa-calendar-alt'></i></div>
                        체험레슨 예약접수
                    </a>
                    <a class="nav-link" href="/reservation/list" >
                        <div class="sb-nav-link-icon"><i class='far fa-calendar-alt'></i></div>
                        체험레슨 예약내역
                    </a>
                </div>
                `;
        }

        if (userRole === '관리자') {
            menuHtml += `   
                 <div class="nav">
<!--                    <a class="nav-link collapsed" href="#" data-bs-toggle="collapse" data-bs-target="#trial" aria-expanded="false" aria-controls="trial">-->
<!--                        <div class="sb-nav-link-icon"><i class="far fa-calendar-alt"></i></div>-->
<!--                        체험레슨-->
<!--                        <div class="sb-sidenav-collapse-arrow"><i class="fas fa-angle-down"></i></div>-->
<!--                    </a>-->
<!--                    <div class="collapse" id="trial" aria-labelledby="headingOne" data-bs-parent="#sidenavAccordionPages">-->
<!--                        <nav class="sb-sidenav-menu-nested nav">-->
<!--                            <a class="nav-link" href="/reservation">체험레슨 예약접수</a>-->
<!--                            <a class="nav-link" href="/reservation/list">체험레슨 예약내역</a>-->
<!--                        </nav>-->
<!--                    </div>-->
<!--                    -->
<!--                    <a class="nav-link collapsed" href="#" data-bs-toggle="collapse" data-bs-target="#interview" aria-expanded="false" aria-controls="interview">-->
<!--                        <div class="sb-nav-link-icon"><i class="fas fa-comment"></i></div>-->
<!--                        면접-->
<!--                        <div class="sb-sidenav-collapse-arrow"><i class="fas fa-angle-down"></i></div>-->
<!--                    </a>-->
<!--                    <div class="collapse" id="interview" aria-labelledby="headingOne" data-bs-parent="#sidenavAccordionPages">-->
<!--                        <nav class="sb-sidenav-menu-nested nav">-->
<!--                            <a class="nav-link" href="/reservation/interview">면접 예약접수</a>-->
<!--                            <a class="nav-link" href="/reservation/interviewList">면접 예약내역</a>-->
<!--                        </nav>-->
<!--                    </div>-->
<!--                    <a class="nav-link" href="/members" >-->
<!--                        <div class="sb-nav-link-icon"><i class="fa-sharp fa-light fa-user"></i></div>-->
<!--                        회원관리-->
<!--                    </a>-->
                    <a class="nav-link" href="/reservation/roomTour/index" >
                        <div class="sb-nav-link-icon"><i class='far fa-calendar-alt'></i></div>
                        룸투어 신청내역
                    </a>
<!--                    <a class="nav-link" href="/reservation/list" >-->
<!--                        <div class="sb-nav-link-icon"><i class='far fa-calendar-alt'></i></div>-->
<!--                        체험레슨 예약내역-->
<!--                    </a>-->
                 
                    <a class="nav-link" href="/members" >
                        <div class="sb-nav-link-icon"><i class="fa-sharp fa-light fa-user"></i></div>
                        회원관리
                    </a>
                </div>     
                `;
        }

        $('.nav-link .dev').click(function(event){
            event.preventDefault(); // 링크 기본 동작을 막음
            alert('개발 예정입니다.');
        });

        // 생성된 메뉴 HTML을 사이드바에 추가
        sidebar.innerHTML = menuHtml;
    }
})


// $('#adminPage').on('click', function(event) {
//     event.preventDefault();
//     $.ajax({
//         url: '/member/isAdmin',
//         type: 'GET',
//         success: function(response) {
//             if (response) {
//                 // 권한이 있는 경우, 링크 이동
//                 window.location.href = $('#adminPage').attr('href');
//             } else {
//                 // 권한이 없는 경우, 경고 메시지 표시
//                 alert("권한이 없습니다.");
//             }
//         },
//         error: function(xhr, status, error) {
//             alert("권한이 없습니다.")
//         }
//     });
// })
//
// $('#sale').on('click', function(event) {
//     event.preventDefault();
//     $.ajax({
//         url: '/member/isAdminOrManager',
//         type: 'GET',
//         success: function(response) {
//             if (response) {
//                 // 권한이 있는 경우, 링크 이동
//                 window.location.href = $('#sale').attr('href');
//             } else {
//                 // 권한이 없는 경우, 경고 메시지 표시
//                 alert("권한이 없습니다.");
//             }
//         },
//         error: function(xhr, status, error) {
//             alert("권한이 없습니다.")
//         }
//     });
// })
//
// $('#saleResult').on('click', function(event) {
//     event.preventDefault();
//     $.ajax({
//         url: '/member/isAdminOrManager',
//         type: 'GET',
//         success: function(response) {
//             if (response) {
//                 // 권한이 있는 경우, 링크 이동
//                 window.location.href = $('#saleResult').attr('href');
//             } else {
//                 // 권한이 없는 경우, 경고 메시지 표시
//                 alert("권한이 없습니다.");
//             }
//         },
//         error: function(xhr, status, error) {
//             alert("권한이 없습니다.")
//         }
//     });
// })
//
// $('#saleTotalResultChart').on('click', function(event) {
//     event.preventDefault();
//     $.ajax({
//         url: '/member/isAdminOrManager',
//         type: 'GET',
//         success: function(response) {
//             if (response) {
//                 // 권한이 있는 경우, 링크 이동
//                 window.location.href = $('#saleTotalResultChart').attr('href');
//             } else {
//                 // 권한이 없는 경우, 경고 메시지 표시
//                 alert("권한이 없습니다.");
//             }
//         },
//         error: function(xhr, status, error) {
//             alert("권한이 없습니다.")
//         }
//     });
// })
//
// $('#company').on('click', function(event) {
//     event.preventDefault();
//     $.ajax({
//         url: '/member/isAdminOrManager',
//         type: 'GET',
//         success: function(response) {
//             if (response) {
//                 // 권한이 있는 경우, 링크 이동
//                 window.location.href = $('#company').attr('href');
//             } else {
//                 // 권한이 없는 경우, 경고 메시지 표시
//                 alert("권한이 없습니다.");
//             }
//         },
//         error: function(xhr, status, error) {
//             alert("권한이 없습니다.")
//         }
//     });
// })