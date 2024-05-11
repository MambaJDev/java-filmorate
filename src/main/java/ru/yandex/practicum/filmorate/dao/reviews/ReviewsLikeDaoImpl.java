package ru.yandex.practicum.filmorate.dao.reviews;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class ReviewsLikeDaoImpl implements ReviewsLikeDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Integer reviewId, Integer userId) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews_like");

        insert.execute(new MapSqlParameterSource("review_id", reviewId)
                .addValue("user_id", userId)
                .addValue("is_like", true));

        log.info("Пользователь ID = {} добавил лайк отзыву ID = {}", userId, reviewId);
    }

    @Override
    public void addDislike(Integer reviewId, Integer userId) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews_like");

        insert.execute(new MapSqlParameterSource("review_id", reviewId)
                .addValue("user_id", userId)
                .addValue("is_like", false));

        log.info("Пользователь ID = {} добавил дизлайк отзыву ID = {}", userId, reviewId);

    }

    @Override
    public void deleteLike(Integer reviewId, Integer userId) {
        String sql = "delete from reviews_like where is_like = true and review_id = ? and user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        log.info("Пользователь ID = {} удалил лайк отзыву ID = {}", userId, reviewId);
    }

    @Override
    public void deleteDislike(Integer reviewId, Integer userId) {
        String sql = "delete from reviews_like where is_like = false and review_id = ? and user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        log.info("Пользователь ID = {} удалил дизлайк отзыву ID = {}", userId, reviewId);
    }
}