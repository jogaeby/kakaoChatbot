const cardContainer = document.getElementById('cardContainer');
const loading = document.getElementById('loading');
let page = 0;         // 페이지 번호 (0부터 시작)
let isLoading = false;
let hasMore = true;   // 더 불러올 데이터 여부

// 📍 URL에서 상품 ID 가져오기
const urlParams = new URLSearchParams(window.location.search);
const productId = urlParams.get('id');  // 예: ?id=123

// 🚩 카드 렌더링 함수
function renderCard(item, prepend = false) {
    const card = document.createElement('div');
    card.className = 'card shadow-lg border-0 rounded-lg mt-3';
    card.setAttribute('data-product-id', item.id ?? 'N/A');

    card.innerHTML = `
        <img src="${item.images?.[0] ?? '/loginLogo.png'}" alt="image" class="card-img-top">
        <div class="card-body">
            <div><strong>제목:</strong> ${item.title ?? 'N/A'}</div>
            <div><strong>타경번호:</strong> ${item.no ?? 'N/A'}</div>
            <div><strong>물건종류:</strong> ${item.category ?? 'N/A'}</div>
            <div><strong>소재지:</strong> ${item.location ?? 'N/A'}</div>
            <div><strong>감정가:</strong> ${item.price?.toLocaleString() ?? 'N/A'}원</div>
            <div><strong>최저가:</strong> ${item.minPrice?.toLocaleString() ?? 'N/A'}원</div>
            <div><strong>예상 낙찰가:</strong> ${item.expectedPrice?.toLocaleString() ?? 'N/A'}원</div>
            <div><strong>매각 기일:</strong> ${item.saleDate ?? 'N/A'}</div>
            <div><strong>담당자:</strong> ${item.managerName ?? 'N/A'}</div>
            <div><strong>링크:</strong> <a href="${item.link ?? '#'}" target="_blank">상세 보기</a></div>
            <div><strong>작성자:</strong> ${item.memberId ?? 'N/A'}</div>
            <div><strong>작성일:</strong> ${item.createDate ?? 'N/A'}</div>
        </div>
    `;

    // 🚀 상품 ID 우선: 맨 위에 추가 (prepend가 true일 때)
    if (prepend) {
        cardContainer.prepend(card);
    } else {
        cardContainer.appendChild(card);
    }

    return card;
}

// 🚩 특정 상품 ID로 조회 (상단에 렌더링)
async function fetchProductById(productId) {
    try {
        const response = await fetch(`/product/${productId}`);
        if (!response.ok) throw new Error('상품을 찾을 수 없습니다.');
        const product = await response.json();

        renderCard(product, true);  // 🚀 상단에 렌더링
    } catch (error) {
        console.error('❌ 특정 상품 조회 실패:', error);
        alert('해당 상품을 찾을 수 없습니다.');
    }
}

// 🚩 일반 데이터 불러오기 (무한 스크롤)
async function fetchData() {
    if (isLoading || !hasMore) return;
    isLoading = true;
    loading.style.display = 'block';

    try {
        const response = await fetch(`/product/previous?page=${page}&size=10`);
        const data = await response.json();

        // 일반 데이터 렌더링
        if (data.content && data.content.length > 0) {
            data.content.forEach(item => renderCard(item));
            page++;  // 다음 페이지로 이동
        }

        // 마지막 페이지 여부
        if (data.last) {
            hasMore = false;
            loading.innerText = '✅ 모든 데이터를 불러왔습니다.';
        }
    } catch (error) {
        console.error('❌ 데이터 불러오기 실패:', error);
        alert("데이터 불러오기 실패하였습니다.")
        loading.innerText = '로딩중...';
    } finally {
        isLoading = false;
        loading.style.display = 'none';
    }
}

// 🚩 무한 스크롤 감지
window.addEventListener('scroll', () => {
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 10) {
        fetchData();
    }
});

// 🚩 실행 로직
if (productId) {
    fetchProductById(productId);  // 🚀 상품 ID가 있으면 상단에 표시
}

fetchData();  // 일반 데이터 로딩
