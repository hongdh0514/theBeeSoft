// --- 로그인 관련 요소 ---
const idInput = document.getElementById('userId');
const pwInput = document.getElementById('userPw');
const loginButton = document.getElementById('loginBtn'); // 로그인 버튼 ID가 'loginBtn'이라고 가정

// --- 로그인 버튼 이벤트 리스너 ---
if (loginButton) { // 버튼이 존재하는지 확인 후 리스너 추가
    loginButton.addEventListener('click', function() {
        const userId = idInput.value;
        const userPw = pwInput.value;

        const loginData = {
            userId: userId,
            userPw: userPw
        };

        responseArea.textContent = '서버에 데이터를 전송 중...';
        responseArea.style.display = 'block'; // 메시지 보이게 설정

        fetch('/api/user/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        })
        .then(response=> {
        if (!response.ok) {
            return response.text().then(text => {
               throw new Error('로그인 오류 발생: ' + response.status + " " + response.statusText + " - " + text);
            });
        }
            return response.json(); // 성공 시 JSON 파싱
        })
        .then(data => {
            console.log('로그인 성공! 서버 응답:', data);
            alert("로그인 성공! " + data.userId + '님 환영합니다!');
            responseArea.style.display = 'none'; // 성공 시 메시지 숨김
            window.location.href = '/board'; // 로그인 성공 후 게시판으로 이동
        })
        .catch(error => {
            console.error('로그인 Ajax 요청 실패:', error);
            responseArea.textContent = error.message;
            responseArea.style.display = 'block'; // 에러 메시지 표시
        });
    });
    console.log("로그인 버튼 이벤트 리스너 연결됨.");
} else {
    console.error("로그인 버튼(ID: loginBtn)을 찾을 수 없습니다.");
}


/*// --- 회원가입 버튼 이벤트 리스너 ---
if (joinButton) {
    joinButton.addEventListener('click', function() {
        // 클릭 시점의 값 가져오기
        const userId = JoinIdInput.value;
        const userPw = JoinPwInput.value;
        const userRePw = JoinRePwInput.value;

        // trim()으로 앞뒤 공백 제거 후 비교
        if (userPw.trim() !== userRePw.trim()) {
        	checkPw.textContent = '비밀번호가 일치하지 않습니다.';
        	checkPw.style.display = 'block';
        	
            JoinPwInput.value = '';
            JoinRePwInput.value = '';
            
            JoinPwInput.focus();
            return;
        }
        
    	checkPw.textContent = '비밀번호가 일치합니다.';
    	checkPw.style.color = 'green';
    	checkPw.style.display = 'block';

        const JoinData = {
            userId: userId,
            userPw: userPw
        };

        responseArea.textContent = '회원가입 정보 전송 중...';
    	responseArea.style.display = 'block';

        fetch('/api/user/join', { // <<< 회원가입 API 엔드포인트로 수정 필요
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(JoinData)
        })
        .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
               throw new Error('회원가입 오류 발생: ' + response.status + " " + response.statusText + " - " + text);
            });
        }
            return response.json(); // 또는 성공 메시지만 받으면 .text()
        })
        .then(data => {
            console.log('회원가입 성공:', data);
            alert('회원가입이 성공적으로 완료되었습니다!');
            responseArea.style.display = 'none';
            window.location.href = '/board'; // 회원가입 성공 후 로그인 페이지로 이동 등
        })
        .catch(error => {
            console.error('회원가입 Ajax 요청 실패:', error);
            responseArea.textContent = error.message;
            responseArea.style.display = 'block';
        });
    });
    console.log("회원가입 버튼 이벤트 리스너 연결됨.");
} else {
    console.error("회원가입 버튼(ID: joinButton)을 찾을 수 없습니다."); // ID 확인 필요
}*/

$(document).ready(function() {
    $("#joinBtn").click(function() {
		
		alert("test");
		
		// 입력값 가져오기
        var joinId = $("#joinId").val();
        var joinPw = $("#joinPw").val();
        var joinRePw = $("#joinRePw").val();
        
        // 빈 값 체크
        if (!joinId || !joinPw || !joinRePw) {
        	checkPw.textContent = '모든 값을 입력하세요.';
        	checkPw.style.display = 'block';
            return;
        }
        
		// 비밀번호와 비밀번호 확인 값 비교
        if (joinPw !== joinRePw) {
        	checkPw.textContent = '비밀번호가 일치하지 않습니다.';
        	checkPw.style.display = 'block';
            return; // 비밀번호가 다르면 함수 종료
        }
        
    	checkPw.textContent = '비밀번호가 일치합니다.';
    	checkPw.style.color = 'green';
    	checkPw.style.display = 'block';
        
		// User 객체 생성
        var user = {
            userId: joinId,
            userPw: joinPw
        };
        
        // AJAX 요청
        $.ajax({
            type: "POST",
            url: "/api/user/join",
            contentType: "application/json",
            data: JSON.stringify(user),
            success: function(response) {
                if (response === "success") {
            	alert('회원가입이 성공적으로 완료되었습니다!');
            	responseArea.style.display = 'none';
                    window.location.href = "/"; // 홈으로 리다이렉트
                } else {
                    alert("회원가입 실패: 이미 존재하는 아이디입니다.");
                }
            },
            error: function() {
                alert("서버 오류가 발생했습니다.");
            }
        });
    });
});
