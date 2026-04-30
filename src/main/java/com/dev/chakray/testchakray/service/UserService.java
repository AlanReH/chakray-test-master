package com.dev.chakray.testchakray.service;

import com.dev.chakray.testchakray.model.Address;
import com.dev.chakray.testchakray.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class UserService {

    private final List<User> usersList = new ArrayList<>();
    private static final String SECRET = "12345678901234567890123456789012"; // 32 chars

    public UserService() {
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        String uuid3 = UUID.randomUUID().toString();
        User user1 = new User(
                uuid1,
                "user1@mail.com",
                "user1",
                "+525512345678",
                encrypt("12345678"),
                "AARR990101XXX",
                "01-01-2026 00:00:00",
                new Address[] {
                        new Address(1, "workaddress", "Street No. 1", "UK"),
                        new Address(2, "homeaddress", "Street No. 2", "AU")
                }
        );
        User user2 = new User(
                uuid2,
                "user2@mail.com",
                "user2",
                "+525523456789",
                encrypt("23456789"),
                "AARR990101YYY",
                "01-01-2026 10:00:00",
                new Address[] {
                        new Address(1, "workaddress", "Street No. 11", "UK"),
                        new Address(2, "homeaddress", "Street No. 22", "AU")
                }
        );
        User user3 = new User(
                uuid3,
                "user3@mail.com",
                "user3",
                "+525534567890",
                encrypt("34567890"),
                "AARR990101ZZZ",
                "01-01-2026 20:00:00",
                new Address[] {
                        new Address(1, "workaddress", "Street No. 111", "UK"),
                        new Address(2, "homeaddress", "Street No. 222", "AU")
                }
        );

        usersList.add(user1);
        usersList.add(user2);
        usersList.add(user3);
    }

    public User login(String taxId, String password) {

        return usersList.stream()
                .filter(u -> u.getTax_id().equals(taxId))
                .findFirst()
                .map(user -> {

                    String encryptedInput = encrypt(password);

                    if (!user.getPassword().equals(encryptedInput)) {
                        throw new IllegalArgumentException("Invalid credentials");
                    }

                    return new User(
                            user.getId(),
                            user.getEmail(),
                            user.getName(),
                            user.getPhone(),
                            null,
                            user.getTax_id(),
                            user.getCreated_at(),
                            user.getAddresses()

                    );

                })
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<User> getUsers(String sortedBy, String filter) {

        List<User> users = new ArrayList<>(usersList);

        // SORTED BY
        if (sortedBy != null && !sortedBy.trim().isEmpty()) {
            users = switch (sortedBy) {
                case "email" -> users.stream().sorted(Comparator.comparing(User::getEmail)).toList();
                case "id" -> users.stream().sorted(Comparator.comparing(User::getId)).toList();
                case "name" -> users.stream().sorted(Comparator.comparing(User::getName)).toList();
                case "phone" -> users.stream().sorted(Comparator.comparing(User::getPhone)).toList();
                case "tax_id" -> users.stream().sorted(Comparator.comparing(User::getTax_id)).toList();
                case "created_at" -> users.stream().sorted(Comparator.comparing(User::getCreated_at)).toList();
                default -> throw new IllegalArgumentException("sortedBy value not valid");
            };
        }

        // FILTER
        if (filter != null) {
             if (filter.isEmpty()) {
                 throw new IllegalArgumentException("missing filter value (cannot be empty)");
             }

            String[] filterParts = filter.contains("+")
                    ? filter.split("\\+")
                    : filter.split(" ");

            if (filterParts.length != 3) {
                throw new IllegalArgumentException("filter formed wrong - must be 'field+condition+value' ");
            }

            String field = filterParts[0];
            String condition = filterParts[1];
            String conditionValue = filterParts[2];

            Function<User, String> filterField = switch (field) {
                case "email" -> User::getEmail;
                case "id" -> User::getId;
                case "name" -> User::getName;
                case "phone" -> User::getPhone;
                case "tax_id" -> User::getTax_id;
                case "created_at" -> User::getCreated_at;
                default -> throw new IllegalArgumentException("invalid field");
            };

            users = users.stream()
                    .filter(u -> applyCondition(filterField.apply(u), condition, conditionValue))
                    .toList();
        }

        return users;
    }

    public User createUser(User user) {

        if (!isValidRFC(user.getTax_id())) {
            throw new IllegalArgumentException("Invalid RFC");
        }

        if (!isValidPhone(user.getPhone())) {
            throw new IllegalArgumentException("Invalid phone");
        }

        boolean exists = usersList.stream()
                .anyMatch(u -> u.getTax_id().equals(user.getTax_id()));

        if (exists) {
            throw new IllegalArgumentException("User with this tax_id already exists");
        }

        user.setId(UUID.randomUUID().toString());
        user.setPassword(encrypt(user.getPassword()));

        // Madagascar Timezone
        ZonedDateTime nowMadagascar = ZonedDateTime.now(ZoneId.of("Indian/Antananarivo"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formattedDate = nowMadagascar.format(formatter);

        user.setCreated_at(formattedDate);

        usersList.add(user);

        return user;
    }

    public User updateUser(String userId, User user) {

        return usersList.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .map(userToUpdate -> {

                    if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                        userToUpdate.setEmail(user.getEmail());
                    }

                    if (user.getName() != null && !user.getName().trim().isEmpty()) {
                        userToUpdate.setName(user.getName());
                    }

                    if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
                        userToUpdate.setPhone(user.getPhone());
                    }

                    if (user.getTax_id() != null && !user.getTax_id().trim().isEmpty()) {
                        userToUpdate.setTax_id(user.getTax_id());
                    }

                    if (user.getCreated_at() != null && !user.getCreated_at().trim().isEmpty()) {
                        userToUpdate.setCreated_at(user.getCreated_at());
                    }

                    return userToUpdate;
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void deleteUser(String userId) {

        boolean removed = usersList.removeIf(user -> user.getId().equals(userId));

        if (!removed) {
            throw new IllegalArgumentException("User not found");
        }
    }

    // HELPER METHODS
    public String encrypt(String data) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            throw new RuntimeException("Error encrypting");
        }
    }

    private boolean applyCondition(String filterField, String condition, String conditionValue) {
        if (filterField == null || condition == null || conditionValue == null) return false;
        return switch (condition) {
            case "co" -> filterField.toLowerCase().contains(conditionValue.toLowerCase());
            case "eq" -> filterField.toLowerCase().equals(conditionValue.toLowerCase());
            case "sw" -> filterField.toLowerCase().startsWith(conditionValue.toLowerCase());
            case "ew" -> filterField.toLowerCase().endsWith(conditionValue.toLowerCase());
            default -> false;
        };
    }

    private boolean isValidRFC(String rfc) {
        return rfc != null && rfc.matches("^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$");
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^(\\+\\d{1,3})?\\d{10}$");
    }

}