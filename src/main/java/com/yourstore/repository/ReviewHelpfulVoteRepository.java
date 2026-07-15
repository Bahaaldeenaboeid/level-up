package com.yourstore.repository;

import com.yourstore.entity.Review;
import com.yourstore.entity.ReviewHelpfulVote;
import com.yourstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewHelpfulVoteRepository extends JpaRepository<ReviewHelpfulVote, Long> {

    // Check if a user already voted on a review
    boolean existsByReviewIdAndUserId(Long reviewId, Long userId);

    // Find vote by review and user
    Optional<ReviewHelpfulVote> findByReviewAndUser(Review review, User user);

    // Delete all votes for a review (used when review is deleted)
    void deleteAllByReviewId(Long reviewId);
}