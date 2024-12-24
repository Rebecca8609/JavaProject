package com.example.demo.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.LoginBean;
import com.example.demo.model.PaymentInfo;
import com.example.demo.model.PaymentInfoRepository;
import com.example.demo.model.UserInfo;
import com.example.demo.model.UserInfoRepository;
import com.example.demo.model.AddressInfo;
import com.example.demo.model.AddressInfoRepository;


import jakarta.servlet.http.HttpSession;

@Controller
public class MemberCenterController {
	

    @Autowired
    private UserInfoRepository userInfoRepository;
    
    @Autowired
    private PaymentInfoRepository paymentInfoRepository;
    
    @Autowired
    private AddressInfoRepository addressInfoRepository;
    

    // 顯示個人資料頁面，從資料庫讀取 userId 對應的資料
    @GetMapping("/memberCenter/profile")
    public String showMemberProfile(Model model, HttpSession session) {
        LoginBean user = (LoginBean) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("errorMessage", "請先登入後再訪問個人資料頁面");
            return "redirect:/login";
        }

        UserInfo userInfo = userInfoRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + user.getUserId()));

        model.addAttribute("userInfo", userInfo);
        return "memberCenter/memberProfile";
    }

    @PutMapping("/memberCenter/profile/update")
    public ResponseEntity<UserInfo> updateProfile(@RequestBody UserInfo updatedUserInfo, HttpSession session) {
        LoginBean user = (LoginBean) session.getAttribute("user");

        if (user == null) {
            System.out.println("未找到用戶 Session，請確認用戶是否已登入");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer sessionUserId = user.getUserId();

        Optional<UserInfo> userInfoOptional = userInfoRepository.findById(sessionUserId);
        if (!userInfoOptional.isPresent()) {
            System.out.println("未找到對應的用戶資料，userId: " + sessionUserId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UserInfo existingUserInfo = userInfoOptional.get();

        // 日誌輸出
        System.out.println("更新前的用戶資料: " + existingUserInfo);

        // 防止前端傳遞 "null" 字串
        if ("null".equals(updatedUserInfo.getUserName())) {
            updatedUserInfo.setUserName(null);
        }
        if ("null".equals(updatedUserInfo.getUserPassword())) {
            updatedUserInfo.setUserPassword(null);
        }
        if ("null".equals(updatedUserInfo.getUserTel())) {
            updatedUserInfo.setUserTel(null);
        }
        if ("null".equals(updatedUserInfo.getUserBirthday())) {
            updatedUserInfo.setUserBirthday(null);
        }

        // 更新有新值的欄位
        if (updatedUserInfo.getUserName() != null && !updatedUserInfo.getUserName().isEmpty()) {
            System.out.println("更新用戶名稱: " + updatedUserInfo.getUserName());
            existingUserInfo.setUserName(updatedUserInfo.getUserName());
        }
        if (updatedUserInfo.getUserPassword() != null && !updatedUserInfo.getUserPassword().isEmpty()) {
            System.out.println("更新用戶密碼: " + updatedUserInfo.getUserPassword());
            existingUserInfo.setUserPassword(updatedUserInfo.getUserPassword());
        }
        if (updatedUserInfo.getUserTel() != null && !updatedUserInfo.getUserTel().isEmpty()) {
            System.out.println("更新用戶電話: " + updatedUserInfo.getUserTel());
            existingUserInfo.setUserTel(updatedUserInfo.getUserTel());
        }
        if (updatedUserInfo.getUserBirthday() != null && !updatedUserInfo.getUserBirthday().isEmpty()) {
            System.out.println("更新用戶生日: " + updatedUserInfo.getUserBirthday());
            existingUserInfo.setUserBirthday(updatedUserInfo.getUserBirthday());
        }

        // 更新到資料庫
        userInfoRepository.save(existingUserInfo);

        // 同步更新 Session 中的資料
        LoginBean updatedLoginBean = new LoginBean();
        updatedLoginBean.setUserId(existingUserInfo.getUserId());
        updatedLoginBean.setUserName(existingUserInfo.getUserName());
        updatedLoginBean.setUserEmail(existingUserInfo.getUserEmail());
        updatedLoginBean.setUserPassword(existingUserInfo.getUserPassword());
        updatedLoginBean.setUserTel(existingUserInfo.getUserTel());
        updatedLoginBean.setUserBirthday(existingUserInfo.getUserBirthday());
        session.setAttribute("user", updatedLoginBean);

        System.out.println("更新後的用戶資料: " + existingUserInfo);

        return ResponseEntity.ok(existingUserInfo);
    }
    
    @PutMapping("/memberCenter/password/verify")
    public ResponseEntity<?> verifyOldPassword(HttpSession session, @RequestBody Map<String, String> request) {
        // Debug 確認收到的請求數據
        System.out.println("收到的請求數據: " + request);

        LoginBean user = (LoginBean) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用戶未登入");
        }

        String oldPassword = request.get("oldPassword");

        // Debug 輸入的明文密碼和加密密碼
        System.out.println("輸入的舊密碼: " + oldPassword);
        System.out.println("Session 中的加密密碼: " + user.getUserPassword());

        // 驗證舊密碼是否正確
        if (!BCrypt.checkpw(oldPassword, user.getUserPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("舊密碼錯誤");
        }

        return ResponseEntity.ok("密碼驗證成功");
    }

    @PutMapping("/memberCenter/password/update")
    public ResponseEntity<?> updatePassword(HttpSession session, @RequestBody Map<String, String> request) {
        System.out.println("接收到的請求數據: " + request);

        LoginBean user = (LoginBean) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用戶未登入");
        }

        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("新密碼不能為空");
        }

        // 驗證密碼規則
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,20}$";
        if (!newPassword.matches(passwordPattern)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("密碼必須包含至少一個大寫字母、一個小寫字母和一個數字，長度為8到20個字元");
        }

        // 加密新密碼
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        // 更新密碼到 LoginBean
        user.setUserPassword(hashedPassword);

        // 更新到資料庫
        Optional<UserInfo> userInfoOptional = userInfoRepository.findById(user.getUserId());
        if (!userInfoOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用戶資料不存在，請聯絡管理員");
        }

        UserInfo userInfo = userInfoOptional.get();
        userInfo.setUserPassword(hashedPassword); // 將新密碼設置到資料庫模型
        userInfoRepository.save(userInfo); // 保存到資料庫

        // 更新 Session
        session.setAttribute("user", user);

        return ResponseEntity.ok("密碼更新成功");
    }
    
    @GetMapping("/memberCenter/payment/list")
    public ResponseEntity<?> getPaymentMethods(HttpSession session) {
        // 從 Session 中獲取當前登入用戶
        LoginBean user = (LoginBean) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用戶未登入");
        }

        // 查詢與用戶 ID 關聯的付款方式
        List<PaymentInfo> paymentMethods = paymentInfoRepository.findByUserId(user.getUserId());
        if (paymentMethods.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList()); // 返回空列表
        }

        return ResponseEntity.ok(paymentMethods); // 返回付款方式列表
    }
    
    @GetMapping("/memberCenter/payment/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Integer id, HttpSession session) {
        LoginBean user = (LoginBean) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
        }

        PaymentInfo payment = paymentInfoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("找不到該付款方式"));

        // 檢查是否屬於當前用戶
        if (!payment.getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("無權查看此付款方式");
        }

        return ResponseEntity.ok(payment); // 返回正確的付款資料
    }
    
    @PostMapping("/memberCenter/payment/add")
    public ResponseEntity<?> addPayment(@RequestBody PaymentInfo payment, HttpSession session) {
        LoginBean user = (LoginBean) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "請先登入"));
        }

        try {
            // 設置 userId
            payment.setUserId(user.getUserId());

            // 設置預設狀態
            payment.setPaymentStatus(1);

            // 保存到資料庫
            paymentInfoRepository.save(payment);

            return ResponseEntity.ok(Map.of("message", "新增付款方式成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "新增付款方式失敗"));
        }
    }
    
    @PutMapping("/memberCenter/payment/update/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable Integer id, 
                                           @RequestBody Map<String, Object> updatedFields, 
                                           HttpSession session) {
        LoginBean user = (LoginBean) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
        }

        PaymentInfo payment = paymentInfoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("找不到該付款方式"));

        if (!payment.getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("無權編輯此付款方式");
        }

        if (updatedFields.containsKey("paymentCategory")) {
            payment.setPaymentCategory((Integer) updatedFields.get("paymentCategory"));
        }
        if (updatedFields.containsKey("paymentBank")) {
            payment.setPaymentBank((String) updatedFields.get("paymentBank"));
        }
        if (updatedFields.containsKey("paymentAccount")) {
            payment.setPaymentAccount((String) updatedFields.get("paymentAccount"));
        }

        paymentInfoRepository.save(payment);

        return ResponseEntity.ok(payment);
    }
    
    @PutMapping("/memberCenter/payment/delete/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Integer id, HttpSession session) {
        LoginBean user = (LoginBean) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
        }

        PaymentInfo payment = paymentInfoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("找不到該付款方式"));

        if (!payment.getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("無權刪除此付款方式");
        }

        payment.setPaymentStatus(2); // 設置為已停用
        paymentInfoRepository.save(payment);

        return ResponseEntity.ok(Map.of("message", "付款方式已刪除"));
    }
    
    
    
    
    // 取得當前用戶的所有地址
    @GetMapping("/memberCenter/address/list")
    public ResponseEntity<?> getAddressList(HttpSession session) {
        LoginBean user = (LoginBean) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "用戶未登入"));
        }

        try {
            List<AddressInfo> addresses = addressInfoRepository.findByUserId(user.getUserId());
            return ResponseEntity.ok(Map.of("addresses", addresses.isEmpty() ? Collections.emptyList() : addresses));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "加載地址列表失敗"));
        }
    }

    // 透過 ID 取得地址
    @GetMapping("/memberCenter/address/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable Integer id, HttpSession session) {
        LoginBean user = (LoginBean) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "請先登入"));
        }

        try {
            AddressInfo address = addressInfoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("找不到該地址"));

            if (!address.getUserId().equals(user.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "無權查看此地址"));
            }

            return ResponseEntity.ok(address);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "取得地址失敗"));
        }
    }

    // 新增地址
    @PostMapping("/memberCenter/address/add")
    public ResponseEntity<?> addAddress(@RequestBody AddressInfo address, HttpSession session) {
        LoginBean user = (LoginBean) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "請先登入"));
        }

        try {
            // 輸出接收到的數據進行調試
            System.out.println("接收到的地址數據：" + address);

            // 設置 userId 和默認啟用狀態
            address.setUserId(user.getUserId());
            address.setAddressStatus(1); // 默認啟用狀態

            // 保存地址
            AddressInfo savedAddress = addressInfoRepository.save(address);
            return ResponseEntity.ok(savedAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "新增地址失敗"));
        }
    }
    
    //超商
    @PostMapping("/memberCenter/address/storeCallback")
    public String handleStoreCallback(@RequestParam("storeId") String storeId,
                                       @RequestParam("storeName") String storeName,
                                       @RequestParam("storeAddress") String storeAddress,
                                       HttpSession session) {
        session.setAttribute("selectedStore", Map.of(
                "storeId", storeId,
                "storeName", storeName,
                "storeAddress", storeAddress
        ));
        return "redirect:/editAddressPage"; // 回到編輯地址頁面
    }

    // 更新地址
    @PutMapping("/memberCenter/address/update/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Integer id, @RequestBody Map<String, Object> updatedFields,
                                           HttpSession session) {
        LoginBean user = (LoginBean) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "請先登入"));
        }

        try {
            AddressInfo address = addressInfoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("找不到該地址"));

            if (!address.getUserId().equals(user.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "無權編輯此地址"));
            }

            // 動態更新數據字段
            if (updatedFields.containsKey("userTel")) {
                address.setUserTel((String) updatedFields.get("userTel"));
            }
            if (updatedFields.containsKey("addressee")) {
                address.setAddressee((String) updatedFields.get("addressee"));
            }
            if (updatedFields.containsKey("addressCategory")) {
                address.setAddressCategory(Integer.parseInt(updatedFields.get("addressCategory").toString()));
            }
            if (updatedFields.containsKey("storeId")) {
                address.setStoreId(Integer.parseInt(updatedFields.get("storeId").toString()));
            }
            if (updatedFields.containsKey("storeName")) {
                address.setStoreName((String) updatedFields.get("storeName"));
            }
            if (updatedFields.containsKey("storeAddress")) {
                address.setStoreAddress((String) updatedFields.get("storeAddress"));
            }
            if (updatedFields.containsKey("addressStatus")) {
                address.setAddressStatus(Integer.parseInt(updatedFields.get("addressStatus").toString()));
            }

            // 保存更新
            AddressInfo updatedAddress = addressInfoRepository.save(address);
            return ResponseEntity.ok(updatedAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "更新地址失敗"));
        }
    }

    // 刪除地址（邏輯刪除）
    @PutMapping("/memberCenter/address/delete/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Integer id, HttpSession session) {
        LoginBean user = (LoginBean) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "請先登入"));
        }

        try {
            AddressInfo address = addressInfoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("找不到該地址"));

            if (!address.getUserId().equals(user.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "無權刪除此地址"));
            }

            address.setAddressStatus(2);
            AddressInfo deletedAddress = addressInfoRepository.save(address);
            return ResponseEntity.ok(Map.of("message", "地址已刪除", "address", deletedAddress));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "刪除地址失敗"));
        }
    }



    @GetMapping("/memberCenter/purchase")
    public String showMemberPurchase() {
        return "memberCenter/memberPurchase";
    }

    @GetMapping("/memberCenter/order")
    public String showMemberOrder() {
        return "memberCenter/memberOrder";
    }
}