package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.exception.BadRequest;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(
        //properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class IntegrationTests {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final ItemRequestService requestService;
    private final EntityManager em;

    @Test
    void testUserServiceImplCreateUser() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String name = "Test User";
        userDto.setName(name);
        userDto.setEmail(email);

        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(user.getEmail(), email);
        Assertions.assertEquals(user.getName(), name);
    }

    @Test
    void testUserServiceImplUpdateUser() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String name = "Test User";
        userDto.setName(name);
        userDto.setEmail(email);

        userDto = userService.create(userDto);

        String nameNew = "New User";
        userDto.setName(nameNew);
        userService.update(userDto.getId(), userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(user.getEmail(), email);
        Assertions.assertEquals(user.getName(), nameNew);
    }

    @Test
    void testUserServiceImplFindOne() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String name = "Test User";
        userDto.setName(name);
        userDto.setEmail(email);

        userDto = userService.create(userDto);

        UserDto result = userService.findOne(userDto.getId());

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(result.getEmail(), email);
        Assertions.assertEquals(result.getName(), name);
    }

    @Test
    void testUserServiceImplDeleteUser() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String name = "Test User";
        userDto.setName(name);
        userDto.setEmail(email);

        userDto = userService.create(userDto);

        userService.delete(userDto.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);

        Assertions.assertThrows(NoResultException.class, () -> {
            query.setParameter("email", email).getSingleResult();
        });
    }

    @Test
    void testUserServiceFindUserById() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String name = "Test User";
        userDto.setName(name);
        userDto.setEmail(email);

        userDto = userService.create(userDto);

        User userFound = userService.findUserById(userDto.getId());

        Assertions.assertEquals(userFound.getId(), userDto.getId());
        Assertions.assertEquals(userFound.getEmail(), email);
        Assertions.assertEquals(userFound.getName(), name);
    }

    @Test
    void testUserServiceFindUserByIdNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.findUserById(-1L);
        });
    }

    @Test
    void testUserServiceDuplicateEmailFailure() {
        UserDto userDto = new UserDto();
        UserDto userDto2 = new UserDto();
        String email = "test@email.com";
        String name = "Test User";
        userDto.setName(name);
        userDto.setEmail(email);
        userDto2.setName(name);
        userDto2.setEmail("another@email.com");

        userService.create(userDto);
        userDto2 = userService.create(userDto2);
        Long id = userDto2.getId();

        Assertions.assertThrows(ConflictException.class, () -> {
            userService.update(id, userDto);
        });
    }

    @Test
    void testItemServiceCreateItem() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        ItemDto itemDto = new ItemDto();
        String description = "some desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemService.create(userDto.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", name).getSingleResult();

        Assertions.assertNotNull(item.getId());
        Assertions.assertEquals(item.getDescription(), description);
        Assertions.assertEquals(item.getName(), name);
        Assertions.assertEquals(item.getAvailable(), true);
        Assertions.assertEquals(item.getOwner().getEmail(), email);
    }

    @Test
    void testItemServiceCreateItemByRequest() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test request");
        requestDto = requestService.create(userDto.getId(), requestDto);

        ItemDto itemDto = new ItemDto();
        String description = "some desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);
        itemDto.setRequestId(requestDto.getId());

        itemService.create(userDto.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", name).getSingleResult();

        Assertions.assertNotNull(item.getId());
        Assertions.assertEquals(item.getDescription(), description);
        Assertions.assertEquals(item.getName(), name);
        Assertions.assertEquals(item.getAvailable(), true);
        Assertions.assertEquals(item.getOwner().getEmail(), email);
        Assertions.assertEquals(item.getRequestId(), requestDto.getId());
    }

    @Test
    void testRequestServiceFindOneWithItems() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Test request");
        requestDto = requestService.create(userDto.getId(), requestDto);

        ItemDto itemDto = new ItemDto();
        String description = "some desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);
        itemDto.setRequestId(requestDto.getId());

        itemDto = itemService.create(userDto.getId(), itemDto);
        ItemRequestWithItemsDto result = requestService.findOne(requestDto.getId());

        Assertions.assertEquals(result.getItems().getFirst().getName(), "Test Item");
        Assertions.assertEquals(result.getItems().getFirst().getId(), itemDto.getId());
        Assertions.assertEquals(result.getItems().getFirst().getOwnerId(), userDto.getId());
    }

    @Test
    void testItemServiceUpdateItem() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        ItemDto itemDto = new ItemDto();
        String description = "some desc";
        String name = "Test Item";
        String nameNew = "New Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);

        itemDto.setName(nameNew);
        itemService.update(userDto.getId(), itemDto.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", nameNew).getSingleResult();

        Assertions.assertNotNull(item.getId());
        Assertions.assertEquals(item.getDescription(), description);
        Assertions.assertEquals(item.getName(), nameNew);
        Assertions.assertEquals(item.getAvailable(), true);
        Assertions.assertEquals(item.getOwner().getEmail(), email);
    }

    @Test
    void testItemServiceUpdateItemByOtherUser() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);
        Long userId = userDto.getId();

        ItemDto itemDto = new ItemDto();
        String description = "some desc";
        String name = "Test Item";
        String nameNew = "New Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);
        Long itemId = itemDto.getId();

        ItemDto newItemDto = new ItemDto(itemId, nameNew, itemDto.getDescription(), itemDto.getAvailable(),
                itemDto.getRequestId());

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            itemService.update(userId + 1, itemId, newItemDto);
        });
    }

    @Test
    void testItemServiceFindOne() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        ItemDto itemDto = new ItemDto();
        String description = "some desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);

        ItemOwnerDto result = itemService.findOne(userDto.getId(), itemDto.getId());

        Assertions.assertEquals(result.getId(), itemDto.getId());
        Assertions.assertEquals(result.getDescription(), description);
        Assertions.assertEquals(result.getName(), name);
        Assertions.assertEquals(result.getAvailable(), true);
    }

    @Test
    void testItemServiceFindOneWithBookingsAndComments() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "testBooker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        BookingDto bookingDtoNext = bookingService.create(bookerDto.getId(),bookingPostDto);

        BookingPostDto bookingPostDtoPast = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(-2),
                LocalDateTime.now().plusDays(-1));
        BookingDto bookingDtoLast = bookingService.create(bookerDto.getId(),bookingPostDtoPast);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Some comment");

        commentDto = itemService.addComment(bookerDto.getId(), itemDto.getId(), commentDto);

        ItemOwnerDto result = itemService.findOne(userDto.getId(), itemDto.getId());

        Assertions.assertEquals(result.getComments().getFirst().getText(), "Some comment");
        Assertions.assertEquals(result.getLastBooking().getStart(), bookingDtoLast.getStart());
        Assertions.assertEquals(result.getLastBooking().getEnd(), bookingDtoLast.getEnd());
        Assertions.assertEquals(result.getNextBooking().getStart(), bookingDtoNext.getStart());
        Assertions.assertEquals(result.getNextBooking().getEnd(), bookingDtoNext.getEnd());
    }

    @Test
    void testItemServiceFindItemsByOwner() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        ItemDto itemDto = new ItemDto();
        String description = "some desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);

        List<ItemOwnerDto> result = itemService.findItemsByOwner(userDto.getId());

        Assertions.assertEquals(result.get(0).getId(), itemDto.getId());
        Assertions.assertEquals(result.get(0).getDescription(), description);
        Assertions.assertEquals(result.get(0).getName(), name);
        Assertions.assertEquals(result.get(0).getAvailable(), true);
    }

    @Test
    void testItemServiceSearchItemsByText() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);

        List<ItemDto> result = itemService.searchItemsByText("%test%");

        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(result.get(0).getId(), itemDto.getId());
        Assertions.assertEquals(result.get(0).getDescription(), description);
        Assertions.assertEquals(result.get(0).getName(), name);
        Assertions.assertEquals(result.get(0).getAvailable(), true);
    }

    @Test
    void testItemServiceFindItemById() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);

        Item result = itemService.findItemById(itemDto.getId());

        Assertions.assertEquals(result.getId(), itemDto.getId());
        Assertions.assertEquals(result.getDescription(), description);
        Assertions.assertEquals(result.getName(), name);
        Assertions.assertEquals(result.getAvailable(), true);
        Assertions.assertEquals(result.getOwner().getName(), userName);
        Assertions.assertEquals(result.getOwner().getEmail(), email);
    }

    @Test
    void testItemServiceFindItemByIdNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.findItemById(-1L);
        });
    }

    @Test
    void testItemServiceAddCommentWithoutBooking() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);
        Long itemId = itemDto.getId();
        Long userId = userDto.getId();

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Some comment");

        Assertions.assertThrows(BadRequest.class, () -> {
            itemService.addComment(userId, itemId, commentDto);
        });
    }

    @Test
    void testItemServiceAddCommentFutureBooking() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "booker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        Long itemId = itemDto.getId();
        Long userId = userDto.getId();
        Long bookerId = bookerDto.getId();

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));


        BookingDto bookingDto = bookingService.create(bookerDto.getId(),bookingPostDto);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Some comment");

        Assertions.assertThrows(BadRequest.class, () -> {
            itemService.addComment(bookerId, itemId, commentDto);
        });
    }

    @Test
    void testItemServiceAddCommentPastBooking() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "booker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        Long itemId = itemDto.getId();
        Long userId = userDto.getId();
        Long bookerId = bookerDto.getId();

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(-2),
                LocalDateTime.now().plusDays(-1));


        BookingDto bookingDto = bookingService.create(bookerDto.getId(),bookingPostDto);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Some comment");

        commentDto = itemService.addComment(bookerId, itemId, commentDto);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.text = :text", Comment.class);
        Comment comment = query.setParameter("text", "Some comment").getSingleResult();

        Assertions.assertNotNull(comment.getId());
        Assertions.assertEquals(comment.getId(), commentDto.getId());
        Assertions.assertEquals(comment.getText(), "Some comment");
        Assertions.assertEquals(comment.getCreated(), commentDto.getCreated());
        Assertions.assertEquals(comment.getAuthor().getName(), bookerName);
        Assertions.assertEquals(comment.getItem().getName(), name);
    }

    @Test
    void testRequestServiceCreateRequest() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        ItemRequestDto requestDto = new ItemRequestDto();
        String description = "some request";
        requestDto.setDescription(description);

        requestService.create(userDto.getId(), requestDto);

        TypedQuery<ItemRequest> query = em.createQuery("Select r from ItemRequest r where r.description = :description", ItemRequest.class);
        ItemRequest request = query.setParameter("description", description).getSingleResult();

        Assertions.assertNotNull(request.getId());
        Assertions.assertEquals(request.getDescription(), description);
    }

    @Test
    void testRequestServiceFindRequestsByUser() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        ItemRequestDto requestDto = new ItemRequestDto();
        String description = "some request";
        requestDto.setDescription(description);

        requestDto = requestService.create(userDto.getId(), requestDto);
        Long requestId = requestDto.getId();

        List<ItemRequestDto> result = requestService.findRequestsByUser(userDto.getId());

        Assertions.assertEquals(result.get(0).getId(), requestId);
        Assertions.assertEquals(result.get(0).getCreated(), requestDto.getCreated());
        Assertions.assertEquals(result.get(0).getDescription(), description);
    }

    @Test
    void testRequestServiceFindRequestsOtherUsers() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        UserDto userDto2 = new UserDto();
        String email2 = "test2@email.com";
        String userName2 = "Test User 2";
        userDto2.setName(userName2);
        userDto2.setEmail(email2);
        userDto2 = userService.create(userDto2);

        ItemRequestDto requestDto = new ItemRequestDto();
        String description = "some request";
        requestDto.setDescription(description);
        requestDto = requestService.create(userDto.getId(), requestDto);

        ItemRequestDto requestDto2 = new ItemRequestDto();
        String description2 = "some request 2";
        requestDto2.setDescription(description2);
        requestDto2 = requestService.create(userDto2.getId(), requestDto2);


        List<ItemRequestDto> result = requestService.findRequestsOtherUsers(userDto.getId());

        Assertions.assertEquals(result.get(0).getId(), requestDto2.getId());
        Assertions.assertEquals(result.get(0).getCreated(), requestDto2.getCreated());
        Assertions.assertEquals(result.get(0).getDescription(), description2);
    }

    @Test
    void testRequestServiceFindOne() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        ItemRequestDto requestDto = new ItemRequestDto();
        String description = "some request";
        requestDto.setDescription(description);

        requestDto = requestService.create(userDto.getId(), requestDto);
        Long requestId = requestDto.getId();

        ItemRequestWithItemsDto result = requestService.findOne(requestId);

        Assertions.assertEquals(result.getId(), requestId);
        Assertions.assertEquals(result.getCreated(), requestDto.getCreated());
        Assertions.assertEquals(result.getDescription(), description);
    }

    @Test
    void testRequestServiceFindOneNotFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            requestService.findOne(-1L);
        });
    }

    @Test
    void testBookingServiceCreate() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "testBooker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);
        Item item = itemService.findItemById(itemDto.getId());

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));


        BookingDto bookingDto = bookingService.create(bookerDto.getId(),bookingPostDto);


        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.item = :item", Booking.class);
        Booking booking = query.setParameter("item", item).getSingleResult();

        Assertions.assertNotNull(booking.getId());
        Assertions.assertEquals(booking.getItem().getName(), name);
        Assertions.assertEquals(booking.getBooker().getName(), bookerName);
        Assertions.assertEquals(booking.getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(booking.getStart(), bookingDto.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDto.getEnd());
    }

    @Test
    void testBookingServiceCreateUnavailable() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "testBooker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(false);

        itemDto = itemService.create(userDto.getId(), itemDto);
        Long bookerId = bookerDto.getId();

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        Assertions.assertThrows(BadRequest.class, () -> {
            bookingService.create(bookerId, bookingPostDto);
        });
    }

    @Test
    void testBookingServiceCreateEndBeforeStart() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "testBooker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(false);

        itemDto = itemService.create(userDto.getId(), itemDto);
        Long userId = userDto.getId();

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1));

        Assertions.assertThrows(BadRequest.class, () -> {
            bookingService.create(userId, bookingPostDto);
        });

    }

    @Test
    void testBookingServiceFindOne() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "testBooker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));


        BookingDto bookingDto = bookingService.create(bookerDto.getId(),bookingPostDto);
        BookingDto result = bookingService.findOne(bookerDto.getId(), bookingDto.getId());

        Assertions.assertEquals(result.getId(), bookingDto.getId());
        Assertions.assertEquals(result.getBooker().getName(), bookerName);
        Assertions.assertEquals(result.getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(result.getStart(), bookingDto.getStart());
        Assertions.assertEquals(result.getEnd(), bookingDto.getEnd());
        Assertions.assertEquals(result.getItem().getName(), itemDto.getName());
    }

    @Test
    void testBookingServiceFindOneUnauthorized() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "testBooker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));


        BookingDto bookingDto = bookingService.create(bookerDto.getId(),bookingPostDto);

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            bookingService.findOne(-1L, bookingDto.getId());
        });
    }

    @Test
    void testBookingServiceApprove() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "testBooker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);
        Item item = itemService.findItemById(itemDto.getId());

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));


        BookingDto bookingDto = bookingService.create(bookerDto.getId(),bookingPostDto);
        bookingService.approve(userDto.getId(), bookingDto.getId(), true);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.item = :item", Booking.class);
        Booking booking = query.setParameter("item", item).getSingleResult();

        Assertions.assertEquals(booking.getId(), bookingDto.getId());
        Assertions.assertEquals(booking.getItem().getName(), name);
        Assertions.assertEquals(booking.getBooker().getName(), bookerName);
        Assertions.assertEquals(booking.getStatus(), BookingStatus.APPROVED);
        Assertions.assertEquals(booking.getStart(), bookingDto.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDto.getEnd());
    }

    @Test
    void testBookingServiceApproveOtherUser() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "testBooker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);

        Long bookerId = bookerDto.getId();

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));


        BookingDto bookingDto = bookingService.create(bookerDto.getId(),bookingPostDto);


        Assertions.assertThrows(UnauthorizedException.class, () -> {
            bookingService.approve(bookerId, bookingDto.getId(), true);
        });
    }

    @Test
    void testBookingServiceReject() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "testBooker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);
        Item item = itemService.findItemById(itemDto.getId());

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));


        BookingDto bookingDto = bookingService.create(bookerDto.getId(),bookingPostDto);
        bookingService.approve(userDto.getId(), bookingDto.getId(), false);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.item = :item", Booking.class);
        Booking booking = query.setParameter("item", item).getSingleResult();

        Assertions.assertEquals(booking.getId(), bookingDto.getId());
        Assertions.assertEquals(booking.getItem().getName(), name);
        Assertions.assertEquals(booking.getBooker().getName(), bookerName);
        Assertions.assertEquals(booking.getStatus(), BookingStatus.REJECTED);
        Assertions.assertEquals(booking.getStart(), bookingDto.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingDto.getEnd());
    }

    @Test
    void testBookingServiceFindByBookerId() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "testBooker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);
        Item item = itemService.findItemById(itemDto.getId());

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));


        BookingDto bookingDto = bookingService.create(bookerDto.getId(),bookingPostDto);

        List<BookingDto> result = bookingService.findByBookerId(bookerDto.getId(),  Optional.ofNullable(BookingState.FUTURE));

        Assertions.assertEquals(result.get(0).getId(), bookingDto.getId());
        Assertions.assertEquals(result.get(0).getItem().getName(), name);
        Assertions.assertEquals(result.get(0).getBooker().getName(), bookerName);
        Assertions.assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(result.get(0).getStart(), bookingDto.getStart());
        Assertions.assertEquals(result.get(0).getEnd(), bookingDto.getEnd());
    }

    @Test
    void testBookingServiceFindByOwnerId() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "testBooker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);
        Item item = itemService.findItemById(itemDto.getId());

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));


        BookingDto bookingDto = bookingService.create(bookerDto.getId(),bookingPostDto);

        List<BookingDto> result = bookingService.findByOwnerId(userDto.getId(),  Optional.ofNullable(BookingState.FUTURE));

        Assertions.assertEquals(result.get(0).getId(), bookingDto.getId());
        Assertions.assertEquals(result.get(0).getItem().getName(), name);
        Assertions.assertEquals(result.get(0).getBooker().getName(), bookerName);
        Assertions.assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(result.get(0).getStart(), bookingDto.getStart());
        Assertions.assertEquals(result.get(0).getEnd(), bookingDto.getEnd());
    }

    @Test
    void testBookingServiceFindByOwnerIdNotFound() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "testBooker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);
        Long userId = userDto.getId();

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));


        BookingDto bookingDto = bookingService.create(bookerDto.getId(),bookingPostDto);

        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.findByOwnerId(userId,  Optional.ofNullable(BookingState.PAST));
        });
    }

    @Test
    void testBookingServiceFindByBookerIdNotFound() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "testBooker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);
        Long userId = userDto.getId();

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));


        BookingDto bookingDto = bookingService.create(bookerDto.getId(),bookingPostDto);
        List<BookingDto> result = bookingService.findByBookerId(userId,  Optional.ofNullable(BookingState.CURRENT));

        System.out.println(result);
        Assertions.assertEquals(result.size(), 0);
    }

    @Test
    void testBookingServicefindBookingById() {
        UserDto userDto = new UserDto();
        String email = "test@email.com";
        String userName = "Test User";
        userDto.setName(userName);
        userDto.setEmail(email);
        userDto = userService.create(userDto);

        UserDto bookerDto = new UserDto();
        String bookerEmail = "testBooker@email.com";
        String bookerName = "Test Booker";
        bookerDto.setName(bookerName);
        bookerDto.setEmail(bookerEmail);
        bookerDto = userService.create(bookerDto);

        ItemDto itemDto = new ItemDto();
        String description = "some test desc";
        String name = "Test Item";
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(true);

        itemDto = itemService.create(userDto.getId(), itemDto);

        BookingPostDto bookingPostDto = new BookingPostDto(itemDto.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));


        BookingDto bookingDto = bookingService.create(bookerDto.getId(),bookingPostDto);

        Booking result = bookingService.findBookingById(bookingDto.getId());

        Assertions.assertEquals(result.getId(), bookingDto.getId());
        Assertions.assertEquals(result.getItem().getName(), name);
        Assertions.assertEquals(result.getBooker().getName(), bookerName);
        Assertions.assertEquals(result.getStatus(), BookingStatus.WAITING);
        Assertions.assertEquals(result.getStart(), bookingDto.getStart());
        Assertions.assertEquals(result.getEnd(), bookingDto.getEnd());
    }
}
