package tn.iset.smartevent.dto;

// Mêmes noms de champs que côté Android (data/api/Api.kt)
public class Dtos {
    public record LoginRequest(String email, String password) {}
    public record RegisterRequest(String name, String email, String password, String phone) {}
    public record AuthResponse(String token, Integer id, String name, String email, String role, String photo) {}
    public record UserDto(Integer id, String name, String email, String role, String photo, String phone) {
        public UserDto(Integer id, String name, String email, String role, String photo) {
            this(id, name, email, role, photo, null);
        }
    }
    public record CategoryDto(Integer id, String name) {}
    public record StatusRequest(String status) {}
    public record RoleRequest(String role) {}
    public record PhotoRequest(String photo) {}
    public record EventDto(Integer id, String title, String description, String date, String time,
                           String location, String category, int maxParticipants, int registered,
                           String status, String ownerEmail, String image) {}
}
