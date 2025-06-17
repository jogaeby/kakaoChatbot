$(document).ready(function () {
    document.getElementById("quotation").addEventListener("click", function () {
        // 현재 URL 가져오기
        let currentUrl = window.location.href;

        // 'deposit'을 'quotation'으로 변경
        let newUrl = currentUrl.replace("/customer/deposit/", "/customer/quotation/");

        // 새로운 URL을 새 창에서 열기
        window.open(newUrl, "_blank");
    });

    document.getElementById("success").addEventListener("click", function () {
        if (confirm("입금 완료 처리를 하시겠습니까?\n입금 완료 처리가 되면 담당자가 확인 후 작업 진행을 시작합니다.")) {
            let currentUrl = new URL(window.location.href);
            const id = currentUrl.pathname.split("/").pop();
            fetch('/customer/success', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({id:id})
            })
                .then(response => {
                    if (!response.ok) {
                        alert("입금처리를 실패하였습니다.")
                        throw new Error("서버 오류");
                    }else {
                        alert("정상적으로 입금처리를 완료하였습니다.")
                        location.reload()
                    }
                }).catch(error => {
                console.error("에러:", error);
            });
        }
    })
});

function execute() {
    if (confirm("해당 접수내역을 배정하시겠습니까?")) {
        const id = $("#receiptId").text();
        const managerPhone = $("#managerPhone").text();

        fetch('/receipt/success', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                id:id,
                managerPhone:managerPhone})
        })
            .then(response => {
                if (!response.ok) {
                    alert("입금처리를 실패하였습니다.")
                    throw new Error("서버 오류");
                }else {
                    alert("정상적으로 입금처리를 완료하였습니다.")
                    location.reload()
                }
            }).catch(error => {
            console.error("에러:", error);
        });
    }
}


