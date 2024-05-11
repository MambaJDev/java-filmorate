package ru.yandex.practicum.filmorate.dao.reviews;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.film.FilmDao;
import ru.yandex.practicum.filmorate.dao.user.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ReviewsDaoImpl implements ReviewsDao {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDao filmDao;
    private final UserDao userDao;


    @Override
    public Review createReview(Review review) {
        filmDao.getFilmById(review.getFilmId().longValue());
        userDao.getUserById(review.getUserId().longValue());

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        int reviewId = (int) insert.executeAndReturnKey(new MapSqlParameterSource("content", review.getContent())
                .addValue("is_positive", review.getIsPositive()));
        review.setReviewId(reviewId);

        SimpleJdbcInsert insert2 = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews_users_films")
                .usingColumns("review_id", "user_id", "film_id");
        insert2.execute(new MapSqlParameterSource("review_id", review.getReviewId())
                .addValue("user_id", review.getUserId())
                .addValue("film_id", review.getFilmId()));

        log.info("Создали отзыв с ID = {}", review.getReviewId());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        filmDao.getFilmById(review.getFilmId().longValue());
        userDao.getUserById(review.getUserId().longValue());

        String sql = "update reviews set content = ?, is_positive = ? where id = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        log.info("Обновили отзыв с ID = {}", review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public List<Review> getAllReviews() {
        try {
            String sql = "select r.id, r.content, r.is_positive, (count(rlt.review_id) - count(rlf.review_id)) as reviewUseful, ruf.user_id, ruf.film_id " +
                    "from reviews as r " +
                    "left join reviews_users_films as ruf on r.id = ruf.review_id " +
                    "left join (select * from reviews_like where is_like = true) as rlt on r.id = rlt.review_id " +
                    "left join (select * from reviews_like where is_like = false) as rlf on r.id = rlf.review_id " +
                    "group by r.id " +
                    "order by reviewUseful desc";

            log.info("Получили список всех отзывов");
            return jdbcTemplate.query(sql, this::mapRow);
        } catch (EmptyResultDataAccessException e) {
            log.error("Отзыв не найден");
            throw new NotFoundException("Отзыв не найден");
        }
    }

    @Override
    public List<Review> getReviewsByFilmId(Integer filmId, Integer count) {
        try {
            String sql = "select r.id, r.content, r.is_positive, (count(rlt.user_id) - count(rlf.user_id)) as reviewUseful, ruf.user_id, ruf.film_id " +
                    "from reviews as r " +
                    "left join reviews_users_films as ruf on r.id = ruf.review_id " +
                    "left join (select * from reviews_like where is_like = true) as rlt on r.id = rlt.review_id " +
                    "left join (select * from reviews_like where is_like = false) as rlf on r.id = rlf.review_id " +
                    "where ruf.film_id = ?" +
                    "group by r.id " +
                    "order by reviewUseful desc " +
                    "limit ?";

            log.info("Получили список всех отзывов у фильма с ID = {} с лимитом = {}", filmId, count);
            return jdbcTemplate.query(sql, this::mapRow, filmId, count);
        } catch (EmptyResultDataAccessException e) {
            log.error("Отзыв не найден");
            throw new NotFoundException("Отзыв не найден");
        }
    }


    @Override
    public Review getReviewById(Integer id) {
        try {
            String sql = "select r.id, r.content, r.is_positive, (count(rlt.user_id) - count(rlf.user_id)) as reviewUseful, ruf.user_id, ruf.film_id " +
                    "from reviews as r " +
                    "left join reviews_users_films as ruf on r.id = ruf.review_id " +
                    "left join (select * from reviews_like where is_like = true) as rlt on r.id = rlt.review_id " +
                    "left join (select * from reviews_like where is_like = false) as rlf on r.id = rlf.review_id " +
                    "where r.id = ?" +
                    "group by r.id ";

            log.info("Получили отзыв под ID = {}", id);
            return jdbcTemplate.queryForObject(sql, this::mapRow, id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Отзыв не найден");
            throw new NotFoundException("Отзыв не найден");
        }
    }

    @Override
    public void deleteAllReviews() {
        String sql = "delete from reviews";
        jdbcTemplate.update(sql);
        log.info("Удалили все отзывы");
    }

    @Override
    public void deleteReviewById(Integer id) {
        String sql = "delete from reviews where id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Удалили отзыв под ID = {}", id);
    }


    private Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();

        review.setReviewId(rs.getInt("id"));
        review.setContent(rs.getString("content"));
        review.setIsPositive(rs.getBoolean("is_positive"));
        review.setUserId(rs.getInt("user_id"));
        review.setFilmId(rs.getInt("film_id"));
        review.setUseful(rs.getInt("reviewUseful"));

        return review;
    }
}