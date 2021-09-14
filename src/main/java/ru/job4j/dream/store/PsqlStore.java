package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PsqlStore implements Store {
    private final static Logger LOGGER = Logger.getLogger(PsqlStore.class.getName());
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        String fileName = "db.properties";
        try (BufferedReader io = new BufferedReader(new FileReader(fileName))) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM post")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    int id = it.getInt("id");
                    String name = it.getString("name");
                    posts.add(new Post(id, name));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
        return posts;
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        String sql = "SELECT * FROM candidate";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    int id = it.getInt("id");
                    int cityId = it.getInt("city_id");
                    String name = it.getString("name");
                    candidates.add(new Candidate(id, cityId, name));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
        return candidates;
    }

    @Override
    public Collection<Post> findTodayPosts() {
        List<Post> posts = new ArrayList<>();
        String sql = "" +
                "SELECT * " +
                "FROM post " +
                "WHERE created >= now() - interval '24 hour'";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    int id = it.getInt("id");
                    String name = it.getString("name");
                    posts.add(new Post(id, name));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
        return posts;
    }

    @Override
    public Collection<Candidate> findTodayCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        String sql = "" +
                "SELECT * " +
                "FROM candidate " +
                "WHERE created >= now() - interval '24 hour'";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    int id = it.getInt("id");
                    int cityId = it.getInt("city_id");
                    String name = it.getString("name");
                    candidates.add(new Candidate(id, cityId, name));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
        return candidates;
    }

    @Override
    public Collection<City> findAllCities() {
        List<City> cities = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM city")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    int id = it.getInt("id");
                    String name = it.getString("name");
                    cities.add(new City(id, name));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
        return cities;
    }

    @Override
    public Collection<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM users")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    int id = it.getInt("id");
                    String name = it.getString("name");
                    String email = it.getString("email");
                    String password = it.getString("password");
                    users.add(new User(id, name, email, password));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
        return users;
    }

    @Override
    public void save(Post post) {
        if (post.getId() == 0) {
            create(post);
        } else {
            update(post);
        }
    }

    @Override
    public void save(User user) {
        if (user.getId() == 0) {
            create(user);
        } else {
            update(user);
        }
    }

    @Override
    public void save(Candidate candidate) {
        if (candidate.getId() == 0) {
            create(candidate);
        } else {
            update(candidate);
        }
    }

    private Candidate create(Candidate candidate) {
        String sql = "INSERT INTO candidate(city_id, name) VALUES (?, ?)";
        int keys = PreparedStatement.RETURN_GENERATED_KEYS;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, keys)
        ) {
            ps.setInt(1, candidate.getCityId());
            ps.setString(2, candidate.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
        return candidate;
    }

    private void update(Candidate candidate) {
        String sql = "UPDATE candidate SET city_id = ?, name = ? WHERE id = ?";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setInt(1, candidate.getCityId());
            ps.setString(2, candidate.getName());
            ps.setInt(3, candidate.getId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
    }

    private Post create(Post post) {
        String sql = "INSERT INTO post(name) VALUES (?)";
        int keys = PreparedStatement.RETURN_GENERATED_KEYS;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, keys)
        ) {
            ps.setString(1, post.getName());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
        return post;
    }

    private void update(Post post) {
        String sql = "UPDATE post SET name = ? WHERE id = ?";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setString(1, post.getName());
            ps.setInt(2, post.getId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
    }

    private User create(User user) {
        String sql = "INSERT INTO users(name, email, password) VALUES (?, ?, ?)";
        int keys = PreparedStatement.RETURN_GENERATED_KEYS;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, keys)
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
        return user;
    }

    private void update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        String sql = "SELECT * FROM post WHERE id=?";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    post = new Post(id, it.getString("name"));
                }

            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
        return post;
    }

    @Override
    public User findUserById(int id) {
        User user = null;
        String sql = "SELECT * FROM users WHERE id=?";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    String name = it.getString("name");
                    String email = it.getString("email");
                    String password = it.getString("password");
                    user = new User(id, name, email, password);
                }

            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
        return user;
    }

    @Override
    public Candidate findCandidateById(int id) {
        Candidate candidate = null;
        String sql = "SELECT * FROM candidate WHERE id=?";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    int cityId = it.getInt("city_id");
                    String name = it.getString("name");
                    candidate = new Candidate(id, cityId, name);
                }

            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
        return candidate;
    }

    @Override
    public User findUserByEmail(String email) {
        User user = null;
        String sql = "SELECT * FROM users WHERE email=?";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setString(1, email);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    int id = it.getInt("id");
                    String name = it.getString("name");
                    String password = it.getString("password");
                    user = new User(id, name, email, password);
                }

            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
        return user;
    }

    @Override
    public void removeCandidate(int id) {
        String sql = "DELETE FROM candidate WHERE id=?";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            ps.execute();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }
    }

    @Override
    public void removeUser(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            ps.execute();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown", e);
        }

    }
}