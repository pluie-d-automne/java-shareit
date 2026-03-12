package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnauthorizedException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.*;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class ShareItTests {

	@Test
	void testUserServiceImplFindUserById() {
		UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
		UserMapper userMapper = new UserMapper();
		UserServiceImpl userService = new UserServiceImpl(mockUserRepository, userMapper);

		long id = 1;
		String email = "test@email.ru";
		String name = "test";
		User testUser = new User();
		testUser.setEmail(email);
		testUser.setId(id);
		testUser.setName(name);

		Mockito.when(mockUserRepository.findById(id))
				.thenAnswer(invocationOnMock -> {
					return Optional.ofNullable(testUser);
				});
		User returnedUser = userService.findUserById(id);

		Assertions.assertEquals(returnedUser.getEmail(), email);
		Assertions.assertEquals(returnedUser.getId(), id);
		Assertions.assertEquals(returnedUser.getName(), name);
	}

	@Test
	void testUserServiceImplFindUserByIdNotFound() {
		UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
		UserMapper userMapper = new UserMapper();
		UserServiceImpl userService = new UserServiceImpl(mockUserRepository, userMapper);

		Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
				.thenReturn(Optional.ofNullable(null));

		Assertions.assertThrows(NotFoundException.class, () -> {
			userService.findUserById(1L);
		});
	}

	@Test
	void testUserServiceCreateUser() {
		UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
		UserMapper userMapper = new UserMapper();
		UserServiceImpl userService = new UserServiceImpl(mockUserRepository, userMapper);

		long id = 1;
		String email = "test@email.ru";
		String name = "test";
		UserDto testUserDto = new UserDto();
		User testUser = new User();
		testUserDto.setEmail(email);
		testUserDto.setName(name);
		testUser.setEmail(email);
		testUser.setId(id);
		testUser.setName(name);

		Mockito.when(mockUserRepository.save(Mockito.any(User.class)))
				.thenAnswer(invocationOnMock -> {
					return testUser;
				});
		UserDto returnedUserDto = userService.create(testUserDto);

		Assertions.assertEquals(returnedUserDto.getEmail(), email);
		Assertions.assertEquals(returnedUserDto.getId(), id);
		Assertions.assertEquals(returnedUserDto.getName(), name);
	}

	@Test
	void testUserServiceUpdateUser() {
		UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
		UserMapper userMapper = new UserMapper();
		UserServiceImpl userService = new UserServiceImpl(mockUserRepository, userMapper);

		long id = 1;
		String email = "test@email.ru";
		String name = "test";
		String newName = "testNew";
		UserDto testUserDto = new UserDto();
		User testUser = new User();
		testUserDto.setEmail(email);
		testUserDto.setName(name);
		testUser.setEmail(email);
		testUser.setId(id);
		testUser.setName(name);

		Mockito.when(mockUserRepository.findById(id))
				.thenAnswer(invocationOnMock -> {
					return Optional.ofNullable(testUser);
				});

		Mockito.when(mockUserRepository.save(Mockito.any(User.class)))
				.thenAnswer(invocationOnMock -> {
					testUser.setName(newName);
					return testUser;
				});
		UserDto returnedUserDto = userService.update(id, testUserDto);

		Assertions.assertEquals(returnedUserDto.getEmail(), email);
		Assertions.assertEquals(returnedUserDto.getId(), id);
		Assertions.assertEquals(returnedUserDto.getName(), newName);
	}

	@Test
	void testUserServiceDelete() {
		UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
		UserMapper userMapper = new UserMapper();
		UserServiceImpl userService = new UserServiceImpl(mockUserRepository, userMapper);

		long id = 1L;
		String email = "test@email.ru";
		String name = "test";
		User testUser = new User();
		testUser.setName(name);
		testUser.setEmail(email);
		testUser.setId(id);

		Mockito.when(mockUserRepository.findById(Mockito.anyLong()))
				.thenAnswer(invocationOnMock -> {
					Long userId =  invocationOnMock.getArgument(0, Long.class);
					testUser.setId(userId);
					return Optional.ofNullable(testUser);
				});

		Mockito.doNothing().when(mockUserRepository).delete(Mockito.any(User.class));

		userService.delete(1L);

		Mockito.verify(mockUserRepository, Mockito.times(1))
				.delete(testUser);
		Mockito.verify(mockUserRepository, Mockito.times(1))
				.findById(1L);
		Mockito.verifyNoMoreInteractions(mockUserRepository);
	}

	@Test
	void testUserServiceFindOne() {
		UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
		UserMapper userMapper = new UserMapper();
		UserServiceImpl userService = new UserServiceImpl(mockUserRepository, userMapper);

		long id = 1;
		String email = "test@email.ru";
		String name = "test";

		User testUser = new User();
		testUser.setEmail(email);
		testUser.setId(id);
		testUser.setName(name);

		Mockito.when(mockUserRepository.findById(id))
				.thenAnswer(invocationOnMock -> {
					return Optional.ofNullable(testUser);
				});

		UserDto returnedUserDto = userService.findOne(id);

		Assertions.assertEquals(returnedUserDto.getEmail(), email);
		Assertions.assertEquals(returnedUserDto.getId(), id);
		Assertions.assertEquals(returnedUserDto.getName(), name);
	}

	@Test
	void testRequestServiceFindOne() {
		ItemRequestRepository mockRequestRepository = Mockito.mock(ItemRequestRepository.class);
		UserService mockUserService = Mockito.mock(UserService.class);
		ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
		ItemRequestServiceImpl requestService = new ItemRequestServiceImpl(mockRequestRepository,
				mockUserService,
				Mappers.getMapper(ItemRequestMapper.class),
				mockItemRepository);

		long id = 1;
		String description = "testDesc";
		String requestorName = "Requestor";
		LocalDateTime created = LocalDateTime.now();
		String itemName = "test item";

		User requestor = new User();
		requestor.setId(1L);
		requestor.setName(requestorName);

		Item item = new Item();
		item.setId(1L);
		item.setName(itemName);

		ItemRequest request = new ItemRequest(id, description, requestor, created);

		Mockito.when(mockRequestRepository.findById(id))
				.thenAnswer(invocationOnMock -> {
					return Optional.ofNullable(request);
				});

		Mockito.when(mockItemRepository.findByRequestId(id))
				.thenAnswer(invocationOnMock -> {
					return List.of(item);
				});

		ItemRequestWithItemsDto result = requestService.findOne(id);

		Assertions.assertEquals(result.getId(), 1L);
		Assertions.assertEquals(result.getDescription(), description);
		Assertions.assertEquals(result.getCreated(), created);
		Assertions.assertEquals(result.getItems().size(), 1);
		Assertions.assertEquals(result.getItems().getFirst().getName(), itemName);
		Assertions.assertEquals(result.getItems().getFirst().getId(), 1);
	}

	@Test
	void testRequestServiceFindRequestsOtherUsers() {
		ItemRequestRepository mockRequestRepository = Mockito.mock(ItemRequestRepository.class);
		UserService mockUserService = Mockito.mock(UserService.class);
		ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
		ItemRequestServiceImpl requestService = new ItemRequestServiceImpl(mockRequestRepository,
				mockUserService,
				Mappers.getMapper(ItemRequestMapper.class),
				mockItemRepository);

		long id = 1;
		String description = "testDesc";
		String requestorName = "Requestor";
		LocalDateTime created = LocalDateTime.now();
		String itemName = "test item";

		User requestor = new User();
		requestor.setId(1L);
		requestor.setName(requestorName);

		Item item = new Item();
		item.setId(1L);
		item.setName(itemName);

		ItemRequest request = new ItemRequest(id, description, requestor, created);

		Mockito.when(mockRequestRepository.findByRequestorIdNot(id))
				.thenAnswer(invocationOnMock -> {
					return List.of(request);
				});

		List<ItemRequestDto> result = requestService.findRequestsOtherUsers(id);

		Assertions.assertEquals(result.get(0).getId(), 1L);
		Assertions.assertEquals(result.get(0).getDescription(), description);
		Assertions.assertEquals(result.get(0).getCreated(), created);
	}

	@Test
	void testRequestServiceFindRequestsByUsers() {
		ItemRequestRepository mockRequestRepository = Mockito.mock(ItemRequestRepository.class);
		UserService mockUserService = Mockito.mock(UserService.class);
		ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
		ItemRequestServiceImpl requestService = new ItemRequestServiceImpl(mockRequestRepository,
				mockUserService,
				Mappers.getMapper(ItemRequestMapper.class),
				mockItemRepository);

		long id = 1;
		String description = "testDesc";
		String requestorName = "Requestor";
		LocalDateTime created = LocalDateTime.now();
		String itemName = "test item";

		User requestor = new User();
		requestor.setId(1L);
		requestor.setName(requestorName);

		Item item = new Item();
		item.setId(1L);
		item.setName(itemName);

		ItemRequest request = new ItemRequest(id, description, requestor, created);

		Mockito.when(mockRequestRepository.findByRequestorId(id))
				.thenAnswer(invocationOnMock -> {
					return List.of(request);
				});

		List<ItemRequestDto> result = requestService.findRequestsByUser(id);

		Assertions.assertEquals(result.get(0).getId(), 1L);
		Assertions.assertEquals(result.get(0).getDescription(), description);
		Assertions.assertEquals(result.get(0).getCreated(), created);
	}

	@Test
	void testRequestServiceCreate() {
		ItemRequestRepository mockRequestRepository = Mockito.mock(ItemRequestRepository.class);
		UserService mockUserService = Mockito.mock(UserService.class);
		ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
		ItemRequestServiceImpl requestService = new ItemRequestServiceImpl(mockRequestRepository,
				mockUserService,
				Mappers.getMapper(ItemRequestMapper.class),
				mockItemRepository);

		long id = 1;
		String description = "testDesc";
		String requestorName = "Requestor";
		LocalDateTime created = LocalDateTime.now();
		String itemName = "test item";

		User requestor = new User();
		requestor.setId(1L);
		requestor.setName(requestorName);

		Item item = new Item();
		item.setId(1L);
		item.setName(itemName);


		ItemRequest request = new ItemRequest(id, description, requestor, created);
		ItemRequestDto itemRequestDto = new ItemRequestDto();
		itemRequestDto.setId(1L);
		itemRequestDto.setCreated(request.getCreated());
		itemRequestDto.setDescription(description);

		Mockito.when(mockRequestRepository.save(Mockito.any(ItemRequest.class)))
				.thenReturn(request);

		Mockito.when(mockUserService.findUserById(Mockito.anyLong()))
				.thenReturn(requestor);

		ItemRequestDto result = requestService.create(requestor.getId(), itemRequestDto);

		Assertions.assertEquals(result.getId(), 1L);
		Assertions.assertEquals(result.getDescription(), description);
		Assertions.assertEquals(result.getCreated(), created);
	}

	@Test
	void testRequestServiceFindOneEmpty() {
		ItemRequestRepository mockRequestRepository = Mockito.mock(ItemRequestRepository.class);
		UserService mockUserService = Mockito.mock(UserService.class);
		ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
		ItemRequestServiceImpl requestService = new ItemRequestServiceImpl(mockRequestRepository,
				mockUserService,
				Mappers.getMapper(ItemRequestMapper.class),
				mockItemRepository);

		long id = 1;
		String description = "testDesc";
		String requestorName = "Requestor";
		LocalDateTime created = LocalDateTime.now();
		String itemName = "test item";

		User requestor = new User();
		requestor.setId(1L);
		requestor.setName(requestorName);

		Item item = new Item();
		item.setId(1L);
		item.setName(itemName);

		ItemRequest request = new ItemRequest(id, description, requestor, created);

		Mockito.when(mockRequestRepository.findById(id))
				.thenAnswer(invocationOnMock -> {
					return Optional.ofNullable(null);
				});

		Mockito.when(mockItemRepository.findByRequestId(id))
				.thenAnswer(invocationOnMock -> {
					return List.of(item);
				});

		Assertions.assertThrows(NotFoundException.class, () -> {
			requestService.findOne(id);
		});
	}

	@Test
	void testItemServiceCreate() {
		ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
		CommentRepository mockCommentRepository = Mockito.mock(CommentRepository.class);
		ItemMapper itemMapper = new ItemMapper();
		UserService mockUserService = Mockito.mock(UserService.class);
		BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);

		ItemServiceImpl itemService = new ItemServiceImpl(mockItemRepository,
				mockCommentRepository,
				itemMapper,
				mockUserService,
				mockBookingRepository);

		String userName = "Test user";
		User user = new User();
		user.setName(userName);

		Mockito.when(mockUserService.findUserById(Mockito.anyLong()))
				.thenAnswer(invocationOnMock -> {
					Long userId = invocationOnMock.getArgument(0, Long.class);
					user.setId(userId);
					return user;
				});

		long id = 1;
		String name = "test item";
		String description = "test item desc";
		Boolean available = false;

		ItemDto itemDto = new ItemDto();
		itemDto.setName(name);
		itemDto.setDescription(description);
		itemDto.setAvailable(available);

		Mockito.when(mockItemRepository.save(Mockito.any(Item.class)))
				.thenAnswer(invocationOnMock -> {
					Item foundItem = invocationOnMock.getArgument(0, Item.class);
					foundItem.setId(id);
					return foundItem;
				});

		ItemDto result = itemService.create(2L, itemDto);

		Assertions.assertEquals(result.getId(), id);
		Assertions.assertEquals(result.getName(), name);
		Assertions.assertEquals(result.getDescription(), description);
		Assertions.assertEquals(result.getAvailable(), available);
	}

	@Test
	void testItemServiceUpdate() {
		ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
		CommentRepository mockCommentRepository = Mockito.mock(CommentRepository.class);
		ItemMapper itemMapper = new ItemMapper();
		UserService mockUserService = Mockito.mock(UserService.class);
		BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);

		ItemServiceImpl itemService = new ItemServiceImpl(mockItemRepository,
				mockCommentRepository,
				itemMapper,
				mockUserService,
				mockBookingRepository);

		long id = 1;
		String name = "test item";
		String description = "test item desc";
		Boolean available = false;

		ItemDto itemDto = new ItemDto();
		itemDto.setName(name);
		itemDto.setDescription(description);
		itemDto.setAvailable(available);

		String userName = "Test user";
		User user = new User();
		user.setName(userName);
		user.setId(2L);

		Item item = new Item();
		item.setName(name);
		item.setDescription(description);
		item.setAvailable(available);
		item.setOwner(user);

		Mockito.when(mockItemRepository.findById(Mockito.anyLong()))
				.thenAnswer(invocationOnMock -> {
					Long itemId = invocationOnMock.getArgument(0, Long.class);
					item.setId(itemId);
					return Optional.ofNullable(item);
				});

		Mockito.when(mockItemRepository.save(Mockito.any(Item.class)))
				.thenAnswer(invocationOnMock -> {
					Item foundItem = invocationOnMock.getArgument(0, Item.class);
					foundItem.setId(id);
					return foundItem;
				});

		ItemDto result = itemService.update(2L, 1L, itemDto);

		Assertions.assertEquals(result.getId(), id);
		Assertions.assertEquals(result.getName(), name);
		Assertions.assertEquals(result.getDescription(), description);
		Assertions.assertEquals(result.getAvailable(), available);
	}

	@Test
	void testItemServiceUpdateUnauthorized() {
		ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
		CommentRepository mockCommentRepository = Mockito.mock(CommentRepository.class);
		ItemMapper itemMapper = new ItemMapper();
		UserService mockUserService = Mockito.mock(UserService.class);
		BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);

		ItemServiceImpl itemService = new ItemServiceImpl(mockItemRepository,
				mockCommentRepository,
				itemMapper,
				mockUserService,
				mockBookingRepository);

		long id = 1;
		String name = "test item";
		String description = "test item desc";
		Boolean available = false;

		ItemDto itemDto = new ItemDto();
		itemDto.setName(name);
		itemDto.setDescription(description);
		itemDto.setAvailable(available);

		String userName = "Test user";
		User user = new User();
		user.setName(userName);
		user.setId(1L);

		Item item = new Item();
		item.setName(name);
		item.setDescription(description);
		item.setAvailable(available);
		item.setOwner(user);

		Mockito.when(mockItemRepository.findById(Mockito.anyLong()))
				.thenAnswer(invocationOnMock -> {
					Long itemId = invocationOnMock.getArgument(0, Long.class);
					item.setId(itemId);
					return Optional.ofNullable(item);
				});

		Mockito.when(mockItemRepository.save(Mockito.any(Item.class)))
				.thenAnswer(invocationOnMock -> {
					Item foundItem = invocationOnMock.getArgument(0, Item.class);
					foundItem.setId(id);
					return foundItem;
				});

		Assertions.assertThrows(UnauthorizedException.class, () -> {
			itemService.update(2L, 1L, itemDto);
		});
	}

	@Test
	void testBookingServiceFindBookingById() {
		BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);
		ItemMapper itemMapper = new ItemMapper();
		BookingMapper bookingMapper = new BookingMapper(itemMapper);
		ItemService mockItemService = Mockito.mock(ItemService.class);
		UserService mockUserService = Mockito.mock(UserService.class);


		BookingServiceImpl bookingService = new BookingServiceImpl(mockBookingRepository,
				bookingMapper,
				mockItemService,
				mockUserService);

		Long itemId = 1L;
		String itemName = "Test item";
		Item item = new Item();
		item.setId(itemId);
		item.setName(itemName);

		Long bookerId = 2L;
		String bookerName = "Test booker";
		User booker = new User();
		booker.setId(bookerId);
		booker.setName(bookerName);
		BookingStatus status = BookingStatus.APPROVED;
		LocalDateTime start = LocalDateTime.now().minusDays(2);
		LocalDateTime end = LocalDateTime.now().minusDays(1);

		Booking booking = new Booking();
		booking.setItem(item);
		booking.setBooker(booker);
		booking.setStatus(status);
		booking.setStart(start);
		booking.setEnd(end);

		Mockito.when(mockBookingRepository.findById(Mockito.anyLong()))
				.thenAnswer(invocationOnMock -> {
					Long bookingId = invocationOnMock.getArgument(0, Long.class);
					booking.setId(bookingId);
					return Optional.ofNullable(booking);
				});

		Booking result = bookingService.findBookingById(3L);

		Assertions.assertEquals(result.getId(), 3L);
		Assertions.assertEquals(result.getItem().getId(), 1L);
		Assertions.assertEquals(result.getItem().getName(), itemName);
		Assertions.assertEquals(result.getBooker().getId(), 2L);
		Assertions.assertEquals(result.getBooker().getName(), bookerName);
		Assertions.assertEquals(result.getStatus(), status);
		Assertions.assertEquals(result.getStart(), start);
		Assertions.assertEquals(result.getEnd(), end);
	}
}
