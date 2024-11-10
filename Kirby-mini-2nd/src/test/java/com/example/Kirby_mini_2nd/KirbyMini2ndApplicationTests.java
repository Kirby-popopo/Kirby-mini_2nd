package com.example.Kirby_mini_2nd;

import com.example.Kirby_mini_2nd.repository.entity.Follows;
import com.example.Kirby_mini_2nd.repository.entity.FollowsCompositekey;
import com.example.Kirby_mini_2nd.repository.repo.FollowsRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class KirbyMini2ndApplicationTests {

	@Autowired
	private FollowsRepo followsRepo;

	@Test
	void FindByFollowerId() {
		List<String> followsList = followsRepo.findByFollowerId("1234");
		followsList.forEach(e -> System.out.println(e));
	}


	@Test
	void SaveFollows() {
		Follows follow = new Follows();
		follow.setFollower_id("user123");
		follow.setFollowing_id("user456");
		follow.setFollow_time(LocalDateTime.now());

		// 데이터 저장 테스트
		followsRepo.save(follow);

		// 저장 후 엔터티가 잘 조회되는지 확인
		assert followsRepo.findById(new FollowsCompositekey("user123", "user456")).isPresent();
	}

	@Test
	void DeleteFollows(){
		// 복합키 생성이후 이 둘로 테스트
		FollowsCompositekey key = new FollowsCompositekey("1234", "jioni");
		Optional<Follows> savedFollow = followsRepo.findById(key);
		assert savedFollow.isPresent();

		followsRepo.deleteById(key);

		// 삭제가 성공적으로 이루어졌는지 확인
		Optional<Follows> deletedFollow = followsRepo.findById(key);
		assert deletedFollow.isEmpty();
	}
}
