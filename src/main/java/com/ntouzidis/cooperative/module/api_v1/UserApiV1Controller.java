package com.ntouzidis.cooperative.module.api_v1;

import com.google.common.base.Preconditions;
import com.ntouzidis.cooperative.module.common.pojo.Context;
import com.ntouzidis.cooperative.module.common.enumeration.Client;
import com.ntouzidis.cooperative.module.common.service.SimpleEncryptor;
import com.ntouzidis.cooperative.module.user.entity.CustomUserDetails;
import com.ntouzidis.cooperative.module.user.entity.User;
import com.ntouzidis.cooperative.module.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
public class UserApiV1Controller {

  private Logger logger = LoggerFactory.getLogger(UserApiV1Controller.class);

  private final Context context;
  private final UserService userService;
  private final SimpleEncryptor simpleEncryptor;

  public UserApiV1Controller(UserService userService, SimpleEncryptor simpleEncryptor, Context context) {
    this.userService = userService;
    this.simpleEncryptor = simpleEncryptor;
    this.context = context;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<User> read(
          @RequestParam(name = "id", required = false) Integer id,
          @RequestParam(name = "username", required = false) String username
  ) {
    User user = userService.getOne(id, username);

    if (userService.isAdmin(context.getUser()))
      decryptApiKeys(user);

    return ResponseEntity.ok(user);
  }

  @GetMapping(value = "all", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<User>> readAll() {
    return ResponseEntity.ok(userService.getAll().stream().peek(this::decryptApiKeys).collect(Collectors.toList()));
  }

  @PostMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyRole('TRADER')")
  public ResponseEntity<User> create(
          @RequestParam(value = "username") String username,
          @RequestParam(value = "email") String email,
          @RequestParam(value = "pass") String pass,
          @RequestParam(value = "confirmPass") String confirmPass,
          @RequestParam(value = "PIN") String PIN
  ) {
    Preconditions.checkArgument(pass.equals(confirmPass), "Password doesn't match");
    Preconditions.checkArgument(StringUtils.isNotBlank(email) , "Wrong email");

    LocalDate today = LocalDate.now();
    String dailyPIN = String.valueOf(today.getDayOfMonth() + today.getMonthValue() + today.getYear());
    Preconditions.checkArgument(dailyPIN.equals(PIN), "WRONG PIN");

    User user = userService.createCustomer(username, email, pass);
    // for the ui, to login immediately after creation
    user.setPassword(confirmPass);
    return ResponseEntity.ok(user);
  }

  @PostMapping(value = "/keys", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<User> updateKeys(
          @RequestParam(name = "apiKey", required = false) String apiKey,
          @RequestParam(name = "apiSecret", required = false) String apiSecret
  ) {
    return ResponseEntity.ok(userService.saveKeys(userService.getOne(context.getUserID()), apiKey, apiSecret));
  }

  @PostMapping(value = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<User> updateClient(
          @RequestParam(name = "client", required = false) Client client
  ) {
    return ResponseEntity.ok(userService.updateClient(userService.getOne(context.getUserID()), client));
  }

  @PostMapping(value = "/fixedQty", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<User> setFixedQty(
          @RequestParam("symbol") String symbol,
          @RequestParam("fixedQty") long qty
  ) {
    return ResponseEntity.ok(userService.setFixedQty(userService.getOne(context.getUserID()), symbol, qty));
  }

  @PostMapping(value = "/pass", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<User> updatePass(
          @RequestParam("oldPass") String oldPass,
          @RequestParam("newPass") String newPass,
          @RequestParam("confirmPass") String confirmPass
  ) {
    return ResponseEntity.ok(userService.changePassword(context.getUserID(), newPass, confirmPass));
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<User> delete(
          @PathVariable Integer id
  ) {
      Preconditions.checkArgument(userService.isAdmin(context.getUser()));
      User user = userService.getOne(id);
      userService.delete(user);
      return ResponseEntity.ok(user);
  }

  @GetMapping(value = "/authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<CustomUserDetails> authenticate() {
        return ResponseEntity.ok(context.getCustomUserDetails());
  }

  private void decryptApiKeys(User user) {
    try {
      user.setApiKey(simpleEncryptor.decrypt(user.getApiKey()));
      user.setApiSecret(simpleEncryptor.decrypt(user.getApiSecret()));
    } catch (Exception e) {
      logger.warn(String.format("Api keys decryption %s failed", e.getMessage()));
    }
  }
}
