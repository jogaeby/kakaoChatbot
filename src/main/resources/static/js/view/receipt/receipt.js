$(document).on("click", ".image-clickable", function () {
    const imgUrl = $(this).attr("data-img-url");
    $("#modalImage").attr("src", imgUrl);
});
document.getElementById('imageUpload').addEventListener('change', function () {
    if (this.files.length > 10) {
        alert("이미지는 최대 10장까지만 첨부 가능합니다.");
        this.value = ""; // 선택 초기화
    }
});
function executeAssign() {
    const imageFiles = document.getElementById('imageUpload').files;

    if (!imageFiles || imageFiles.length === 0) {
        alert("이미지를 반드시 첨부해야 합니다.");
        return;
    }

    if (imageFiles.length > 10) {
        alert("이미지는 최대 10장까지만 첨부 가능합니다.");
        return;
    }

    if (confirm("해당 접수내역을 조치완료하시겠습니까?")) {
        $('#loadingOverlay').show();

        const id = $("#receiptId").text();
        const type = $("#type").text();
        const sheetName = $("#sheetName").text();
        // FormData 객체 생성
        const formData = new FormData();
        formData.append("id", id);
        formData.append("type", type);
        formData.append("sheetName", sheetName);
        // 이미지 파일들 추가
        for (let i = 0; i < imageFiles.length; i++) {
            formData.append("images", imageFiles[i]); // 서버에서는 images[]로 받을 수도 있음
        }

        fetch('/receipt/assign', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    alert("❌실패하였습니다.❌");
                    throw new Error("서버 오류");
                } else {
                    alert("✅성공적으로 완료하였습니다.✅");
                    location.reload();
                }
            })
            .catch(error => {
                console.error("에러:", error);
            })
            .finally(() => $('#loadingOverlay').hide());
    }
}

function executeComplete() {
    if (confirm("해당 접수내역을 AS완료처리 하시겠습니까?")) {
        $('#loadingOverlay').show();

        const id = $("#receiptId").text();
        const sheetName = $("#sheetName").text();
        const managerPhone = $("#managerPhone").text();
        const managerName = $("#managerName").text();
        const customerName = $("#customerName").text();
        const customerPhone = $("#customerPhone").text();
        const address = $("#address").text();
        const symptom = $("#symptom").text();
        // const inquiries = $("#inquiries").text();

        fetch('/receipt/complete', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                id:id,
                sheetName:sheetName,
                managerName:managerName,
                managerPhone:managerPhone,
                customerName:customerName,
                customerPhone:customerPhone,
                address:address,
                symptom:symptom,
                // inquiries:inquiries,
            })
        })
            .then(response => {
                if (!response.ok) {
                    alert("❌AS완료처리를 실패하였습니다.❌")
                    throw new Error("서버 오류");
                }else {
                    alert("✅성공적으로 AS완료처리 되었습니다.✅")
                    location.reload()
                }
            }).catch(error => {
            console.error("에러:", error);
        }).finally(() => $('#loadingOverlay').hide());
    }
}
