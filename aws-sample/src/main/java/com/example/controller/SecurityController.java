package com.example.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.data.BusinessHourForm;
import com.example.data.FormData;
import com.example.data.MemberQuery;
import com.example.data.NewPassword;
import com.example.data.PrePassword;
import com.example.data.ReservationForm;
import com.example.entity.AdminUser;
import com.example.entity.AttachedFile;
import com.example.entity.BusinessHour;
import com.example.entity.Facility;
import com.example.entity.Member;
import com.example.entity.PermissionHour;
import com.example.entity.Reservation;
import com.example.entity.TemporaryBusiness;
import com.example.info.ContextPathInfo;
import com.example.info.S3Info;
import com.example.repository.AdminUserRepository;
import com.example.repository.AttachedFileRepository;
import com.example.repository.BusinessHourRepository;
import com.example.repository.FacilityRepository;
import com.example.repository.MemberDaoImpl;
import com.example.repository.MemberRepository;
import com.example.repository.PermissionHourRepository;
import com.example.repository.ReservationRepository;
import com.example.repository.TemporaryBusinessRepository;
import com.example.service.AttachedFileService;
import com.example.service.S3Service;
import com.example.service.TimeListService;
import com.example.util.AuthAdminUser;
import com.example.util.AuthBusiness;
import com.example.util.AuthFacility;
import com.example.util.AuthMember;
import com.example.util.AuthReservation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class SecurityController {

	private final HttpSession session;
    private final AdminUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final FacilityRepository facilityRepository;
    private final ReservationRepository reservationRepository;
    private final AttachedFileRepository attachedFileRepository;
    private final BusinessHourRepository businessHourRepository;
    private final TemporaryBusinessRepository temporaryBusinessRepository;
    private final PermissionHourRepository permissionHourRepository;
    private final S3Service s3Service;
    private final S3Info s3Info;
    private final AttachedFileService attachedFileService;
    private final TimeListService timeListService;
    private final ContextPathInfo contextPathInfo;

    @PersistenceContext
    private EntityManager entityManager;

    MemberDaoImpl memberDaoImpl;
    @PostConstruct
    public void init() {
        memberDaoImpl = new MemberDaoImpl(entityManager);
    }

    // --------------------------------------------------------------------------------
    // ログイン画面とログイン情報　レジスト画面はDB情報が無い場合の臨時手段用
    // --------------------------------------------------------------------------------
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginform(@Validated @ModelAttribute("user") AdminUser user, BindingResult result) {
        if (result.hasErrors())
            return "login";
        return "redirect:/loginUser";
    }

    @GetMapping("/")
    public String showList(Authentication loginUser, Model model) {
        model.addAttribute("username", loginUser.getName());
        model.addAttribute("authority", loginUser.getAuthorities());
        return "loginUser";
    }

    @GetMapping("/register")
    public String register(Model model) {
    	AdminUser user = new AdminUser();
    	user.setAuthAdminUser(AuthAdminUser.EDIT_ADMINUSER);
    	user.setAuthMember(AuthMember.EDIT_MEMBER);
    	user.setAuthFacility(AuthFacility.EDIT_FACILITY);
    	user.setAuthReservation(AuthReservation.EDIT_RESERVATION);
    	user.setAuthBusiness(AuthBusiness.EDIT_BUSINESS);
    	model.addAttribute("user", user);
    	model.addAttribute("newPassword", new NewPassword());
        return "register";
    }

    @PostMapping("/register")
    public String process(@Validated @ModelAttribute("user") AdminUser user, BindingResult result1,
						@Validated @ModelAttribute("newPassword") NewPassword newPass, BindingResult result3
    					) {
    	if (result1.hasErrors() || result3.hasErrors())
            return "register";
    	user.setPassword(passwordEncoder.encode(newPass.getTypePass2()));
        userRepository.save(user);
        return "redirect:/login?register";
    }


    // --------------------------------------------------------------------------------
    // 管理ユーザ
    // --------------------------------------------------------------------------------
    @GetMapping("/adminUser")
    public String showUserList(Model model) {
    	model.addAttribute("users", userRepository.findByDeleteDateTimeIsNull());
        return "adminUser_list";
    }

    @GetMapping("/adminUser/form")
    public String formUser(Model model) {
    	AdminUser user = new AdminUser();
    	//ラジオボタンのための初期値
    	user.setAuthAdminUser(AuthAdminUser.EDIT_ADMINUSER);
    	user.setAuthMember(AuthMember.EDIT_MEMBER);
    	user.setAuthFacility(AuthFacility.EDIT_FACILITY);
    	user.setAuthReservation(AuthReservation.EDIT_RESERVATION);
    	user.setAuthBusiness(AuthBusiness.EDIT_BUSINESS);
    	model.addAttribute("user", user);
    	model.addAttribute("prePassword", new PrePassword());
    	model.addAttribute("newPassword", new NewPassword());
        return "adminUser_form";
    }

    @PostMapping("/adminUser/form")
    public String formUser(@Validated @ModelAttribute("user") AdminUser user, BindingResult result1,
    						@Validated @ModelAttribute("prePassword") PrePassword prePass, BindingResult result2,
    						@Validated @ModelAttribute("newPassword") NewPassword newPass, BindingResult result3
    						){
    	if (result1.hasErrors() || result2.hasErrors() || result3.hasErrors()) {
            return "adminUser_form";
        }

    	if(user.getId() == null || newPass.getUpdate2().equals("true")) {
    		//新規登録またはパスワード更新あり
    		user.setPassword(passwordEncoder.encode(newPass.getTypePass2()));
    	}else{
    		//パスワード更新なし
    		user.setPassword(userRepository.findByIdAndDeleteDateTimeIsNull(user.getId()).get().getPassword());
    	}

        try {
        	userRepository.save(user);
        	return "redirect:/adminUser";
        // @Versionによる楽観ロック
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
        	return "error";
        }
    }

    @GetMapping("/adminUser/{id}")
    public String formUserById(@PathVariable(name = "id") Long id, Model model) {
    	//未作成ID、削除済IDはまとめてNoSuchElementExceptionの例外処理
        try {
        	AdminUser user = userRepository.findByIdAndDeleteDateTimeIsNull(id).get();
        	user.setPassword(null);	//パスワードはクライアント側に渡さない
        	model.addAttribute("user", user);
        	model.addAttribute("prePassword", new PrePassword(id));
        	model.addAttribute("newPassword", new NewPassword(id));
            return "adminUser_form";
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			return "redirect:/adminUser";
		}
    }

    @PostMapping("/adminUser/delete")
    public String deleteUser(@ModelAttribute("user") AdminUser user) {
    	//論理削除した後もしブラウザバックできたとしても該当データを取ってこれずにエラーになる
    	var dltuser = userRepository.findByIdAndDeleteDateTimeIsNull(user.getId()).get();
	    dltuser.setDeleteDateTime(LocalDateTime.now());
	    userRepository.save(dltuser);
        return "redirect:/adminUser";
        //return "redirect:/login"; //自分自身を削除してエラーが起こるようにするなら
    }

	@GetMapping("/adminUser/cancel")
	public String cancelUser() {
		return "redirect:/adminUser";
	}


    // --------------------------------------------------------------------------------
    // 会員管理
    // --------------------------------------------------------------------------------
    @GetMapping("/member")
    @Transactional //Session/EntityManager is closedになるので
    public String showMember(@PageableDefault (page = 0, size = 5, direction = Direction.ASC, sort = "id")
    							Pageable pageable, Model model,
    							@RequestHeader(value = "referer", required = false) final String referer
    							) {
        // sessionから前回の検索条件を取得
    	MemberQuery memberQuery = (MemberQuery)session.getAttribute("querySession");

        // 会員一覧画面以外からの遷移であれば検索値とpageableをリセットする
    	if (referer == null || !(referer.contains("member")) || memberQuery == null) {
        	memberQuery = new MemberQuery();
            session.setAttribute("querySession", memberQuery);
    	}

        // sessionから前回のpageableを取得
    	Pageable prevPageable = (Pageable)session.getAttribute("prevPageable");
    	if (referer == null || !(referer.contains("member/form"))	) {
    		//フォーム画面からの遷移のみセッションのページ情報を使う
    		//想定：会員一覧画面2ページ目から会員を選択しフォーム画面に→戻るボタンで再び2ページ目の一覧画面に
            prevPageable = pageable;
            session.setAttribute("prevPageable", prevPageable);
        }

        Page<Member> memberPage = memberDaoImpl.findByJPQL(memberQuery, prevPageable);
        model.addAttribute("queryModel", memberQuery); //検索データ
        model.addAttribute("pageModel", memberPage); //会員リスト
        model.addAttribute("listUrl", "member"); //ページネーション用のURL文字列
        return "member_list";
    }

    // member検索処理（検索ボタン押下）
    @PostMapping("/member")
    @Transactional
    public String queryMember(@ModelAttribute MemberQuery memberQuery, BindingResult result,
    							@PageableDefault(page = 0, size = 5, direction = Direction.ASC, sort = "id")
    							Pageable pageable, Model model) {
        Page<Member> memberPage = null;
        if ( !(result.hasErrors()) ) {
    		Pageable prevPageable = (Pageable)session.getAttribute("prevPageable");
	    	Pageable pageR = PageRequest.of( 0, prevPageable.getPageSize(), prevPageable.getSort());
	    	//ページ数は0にリセット、ページサイズとソートは引き継ぐ
	    	session.setAttribute("prevPageable", pageR);

            // エラーがなければ検索
            memberPage = memberDaoImpl.findByJPQL(memberQuery, pageR);

            // 入力された検索条件をsessionへ保存
            session.setAttribute("querySession", memberQuery);
            model.addAttribute("queryModel", memberQuery);
            model.addAttribute("pageModel", memberPage);
        } else {
        	model.addAttribute("pageModel", null);
        }
        model.addAttribute("listUrl", "member");
        return "member_list";
    }

    // 新規登録画面
    @GetMapping("/member/form")
    public String memberCreate(@ModelAttribute("member") Member member) {
        return "member_form";
    }

    //登録or更新ボタン押下
    @PostMapping("/member/form")
    public String memberForm(@Validated @ModelAttribute("member") Member member, BindingResult result, Model model) {
    	//既存会員の予約情報をセット
    	if(member.getId()!=null)
    		model.addAttribute("reservations", reservationRepository.findByMemberIdAndDeleteDateTimeIsNull(member.getId()));

        if (result.hasErrors()) {
            return "member_form";
        }
        try {
        	//新規会員 ...一旦パスワード設定なし
        	if(member.getId()==null) {

        	//更新の場合パスワードはそのまま
        	}else {
        		member.setPassword( memberRepository.findById(member.getId()).get().getPassword() );
        	}
        	memberRepository.save(member);
        	return "redirect:/member";
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
        	return "error";
        }
    }

    // 更新画面
    @GetMapping("/member/form/{id}")
    public String showMemberId(@PathVariable(name = "id") Long id, Model model) {
    	Member member = memberRepository.findById(id).get();
    	if ( member.getDeleteDateTime() != null )
    		return "redirect:/member";
    	model.addAttribute("member", member);

    	//会員ごとの有効な予約リストを表示する
    	List<Reservation> list = reservationRepository.findRemainByMemberId(member.getId(), LocalDate.now(), LocalTime.now()); //有効な予約のみ
    	List<ReservationForm> formList = new ArrayList<>();
    	for(Reservation res:list) {
    		Facility facility = facilityRepository.findById(res.getFacilityId()).get();
    		ReservationForm reservationForm = new ReservationForm(res,member,facility);
    		formList.add(reservationForm);
    	}
    	model.addAttribute("reservations", formList);
        return "member_form";
    }

	@GetMapping("/member/cancel")
	public String cancel() {
		return "redirect:/member";
	}


    // --------------------------------------------------------------------------------
    // 施設管理
    // --------------------------------------------------------------------------------
    @GetMapping("/facility")
    public String showFacility(Model model) {
        model.addAttribute("items", facilityRepository.findByDeleteDateTimeIsNullOrderByIdAsc());//findAll());
        model.addAttribute("listUrl", "facility");
        return "facility_list";
    }

    @GetMapping("/facility/form")
    public String formFacility(@ModelAttribute("item") Facility item, Model model) {
    	//ここでfileContentsを初期化してもブラウザバック時にブラウザには参照情報が残っている→Javascriptで対応
    	model.addAttribute("formData", new FormData(contextPathInfo.getContext_path()));
        return "facility_form";
    }

    @PostMapping("/facility/form")
    public String formFacility(@Validated @ModelAttribute("item") Facility item, BindingResult result1,
    							@Validated @ModelAttribute("formData") FormData formData, BindingResult result2,
    							Authentication loginUser, Model model) {
    	//一時保存画像の内、削除ボタンを押してないものだけ詰め直す
    	List<AttachedFile> tmpList = new ArrayList<>();
    	if ( formData.getTempAFs()!=null ) {
    		for(AttachedFile form_af : formData.getTempAFs()) {
				if(form_af.getDelete_pic().equals("no_delete"))
    				tmpList.add(form_af);
    		}
    	}

    	//フォームで入力した画像(複数)を一時保存する
        if (!result2.hasErrors() && formData.getFileContents() != null) {
        	for (MultipartFile multipartFile:formData.getFileContents()) {
        		if (multipartFile.getSize()!=0) {
    	        	int fid = 0;
    	        	if(item.getId()!=null)
    	        		fid = item.getId().intValue();
    	        	AttachedFile tmp_af;
    	        	if(s3Info.getBuckectName()==null || s3Info.getBuckectName().equals("no_bucket")) {
    	        		//ローカルに一時保存
    	        		tmp_af = attachedFileService.tempAttachedFile(fid, multipartFile);
    	        	}else {
    	        		//AWS S3に一時保存
    	        		tmp_af = s3Service.uploadTemp(fid, multipartFile);
    	        	}
    	        	tmpList.add(tmp_af);
    	        }
        	}
        }

        //モデルに再登録
        formData.setTempAFs(tmpList);
	    formData.setContext_path(contextPathInfo.getContext_path());
    	model.addAttribute("formData", formData);

        //バリデーションエラーチェック
        if (result1.hasErrors() || result2.hasErrors())
        	return "facility_form";
        //エラーがあった場合ここで終了
        //一時保存画像の状態や、登録済画像の削除状態等すべて引き継ぐ


    	//登録者と登録時間を記録。画像の更新時にversionが加算されないことへの対応
    	LocalDateTime time = LocalDateTime.now();
    	if(item.getId()==null) { //新規登録
    		item.setCreateDateTime(time);
    		item.setCreateUser(loginUser.getName());
    	}else { //更新
    		item.setUpdateDateTime(time);
    		item.setUpdateUser(loginUser.getName());
    		Facility fa = facilityRepository.findById(item.getId()).get();
    		item.setCreateDateTime(fa.getCreateDateTime());
    		item.setCreateUser(fa.getCreateUser());
    	}

    	//@Versionによる例外処理
        try {
            Long facilityId = facilityRepository.save(item).getId();//新規の時はitem.getId()がnullなのでsaveの戻り値を使う

            //登録済画像の削除ボタンが押されていたら削除する
            if ( formData.getAttachedFiles()!=null ){
	            for (AttachedFile af : formData.getAttachedFiles()) {
	            	if (af.getDelete_pic().equals("do_delete")) {
	    	        	if(s3Info.getBuckectName()==null || s3Info.getBuckectName().equals("no_bucket")) {
	    	        		// ローカルストレージからファイルを削除
	    	        		attachedFileService.deleteAttachedFile(af.getAfId());
	    	        	}else {
	    	        		//AWS S3ストレージからファイルを削除
	    	        		s3Service.delete(af);
	    	        	}
	    	        	af.setDeleteDateTime(LocalDateTime.now());
	    	        	attachedFileRepository.save(af);	// attached_fileテーブルから論理削除
	            	}
	            }
            }

            //一時保存画像を通常保存フォルダに移動し、画像データをDBに保存する
            if (tmpList != null) {
	        	for (AttachedFile af:tmpList) {
	        		af.setForeignId(facilityId.intValue());

    	        	if(s3Info.getBuckectName()==null || s3Info.getBuckectName().equals("no_bucket")) {
    	        		// ローカルストレージ
    	        		try {
    	        			attachedFileService.saveAttachedFile(af);
    					} catch (IOException e) {
    						e.printStackTrace();
    					}
    	        	}else {
    	        		//AWS S3
    	        		s3Service.copyAndSave(af);
    	        	}
	        	}
            }

            //一定時間経ったローカル一時保存画像を削除。
            //誰かが更新処理を完了する度に削除処理が行われるが、タスクスケジューラにする等した方が良いかもしれない
            //AWSだとスケジューラがある
            //attachedFileService.deleteTempAttachedFiles();

        	return "redirect:/facility";
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
        	return "error";
        }
    }

    @GetMapping("/facility/{id}")
    public String facilityById(@PathVariable(name = "id") Long id, Model model) {
    	Facility facility = facilityRepository.findById(id).get();
    	if ( facility.getDeleteDateTime() != null )
    		return "redirect:/facility";
    	FormData formData = new FormData();
	    formData.setAttachedFiles(attachedFileRepository.findByForeignIdAndDeleteDateTimeIsNullOrderByAfId(id.intValue()));
	    formData.setContext_path(contextPathInfo.getContext_path());
	    model.addAttribute("formData", formData);
	    model.addAttribute("item", facility);
        return "facility_form";
    }

	@GetMapping("/facility/cancel")
	public String cancelFacility() {
		return "redirect:/facility";
	}


    // --------------------------------------------------------------------------------
    // 予約管理
    // --------------------------------------------------------------------------------
    @GetMapping("/reservation")
    public String showReservation(Model model) {
    	List<Reservation> list = reservationRepository.findByDeleteDateTimeIsNull();
    	List<ReservationForm> formList = new ArrayList<>();
    	for(Reservation res:list) {
    		Member member = memberRepository.findById(res.getMemberId()).get();
    		Facility facility = facilityRepository.findById(res.getFacilityId()).get();
    		ReservationForm reservationForm = new ReservationForm(res,member,facility);
    		formList.add(reservationForm);
    	}
    	model.addAttribute("items", formList);
        return "reservation_list";
    }

    @GetMapping("/reservation/form")
    public String formReservation(Model model) {
    	//会員と施設を一つ必ずデフォルトセットする。無い場合一覧画面に戻る
    	Optional<Member> op1 = memberRepository.findFirstByDeleteDateTimeIsNull();
    	Optional<Facility> op2 = facilityRepository.findFirstByDeleteDateTimeIsNull();
    	if (op1.isEmpty() || op2.isEmpty())
    		return "redirect:/reservation";
    	ReservationForm reservationForm = new ReservationForm(op1.get(), op2.get());
    	model.addAttribute("item", reservationForm);
    	model.addAttribute("facilities", facilityRepository.findByDeleteDateTimeIsNullOrderByIdAsc());
    	model.addAttribute("members", memberRepository.findByDeleteDateTimeIsNull());
    	model.addAttribute("offday", timeListService.makeOffDayList());
    	model.addAttribute("tonday", timeListService.makeTempOnDayList());
    	model.addAttribute("toffday", timeListService.makeTempOffDayList());
        return "reservation_form";
    }

    @GetMapping("/reservation/member/{m_id}/form")
    public String formReservationByMemberId(@PathVariable(name = "m_id") Long m_id, Model model) {
    	Member member = memberRepository.findById(m_id).get();
    	Optional<Facility> op2 = facilityRepository.findFirstByDeleteDateTimeIsNull();
    	if (member.getDeleteDateTime() != null || op2.isEmpty())
    		return "redirect:/reservation";
    	ReservationForm reservationForm = new ReservationForm(member, op2.get());
    	model.addAttribute("item", reservationForm);
    	model.addAttribute("facilities", facilityRepository.findByDeleteDateTimeIsNullOrderByIdAsc());
    	model.addAttribute("members", memberRepository.findByDeleteDateTimeIsNull());
    	model.addAttribute("offday", timeListService.makeOffDayList());
    	model.addAttribute("tonday", timeListService.makeTempOnDayList());
    	model.addAttribute("toffday", timeListService.makeTempOffDayList());
        return "reservation_form";
    }

    @PostMapping("/reservation/form")
    public String formReservation(@Validated @ModelAttribute("item") ReservationForm item, BindingResult result, Model model) {
    	if(item.getFacility()!=null && item.getRday()!=null)
    		model.addAttribute("timeList", timeListService.makeTimeList(item.getFacility(), item.getRday()));
    	model.addAttribute("offday", timeListService.makeOffDayList());
    	model.addAttribute("tonday", timeListService.makeTempOnDayList());
    	model.addAttribute("toffday", timeListService.makeTempOffDayList());
    	model.addAttribute("facilities", facilityRepository.findByDeleteDateTimeIsNullOrderByIdAsc());
    	model.addAttribute("members", memberRepository.findByDeleteDateTimeIsNull());
        if (result.hasErrors()) {
            return "reservation_form";
        }

        try {
        	Reservation reservation = item.makeReservation();
        	reservationRepository.save(reservation);
        	return "redirect:/reservation";
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
        	return "error";
        }
    }

    @GetMapping("/reservation/{id}")
    public String showReservationId(@PathVariable(name = "id") Long id, Model model) {
    	Reservation reservation = reservationRepository.findById(id).get();
    	if ( reservation.getDeleteDateTime() != null )
    		return "redirect:/reservation";
		Member member = memberRepository.findById(reservation.getMemberId()).get();
		Facility facility = facilityRepository.findById(reservation.getFacilityId()).get();
    	ReservationForm reservationForm = new ReservationForm(reservation, member, facility);
    	model.addAttribute("item", reservationForm);
        return "reservation_form";
    }

    @PostMapping("/reservation/delete")
    public String deleteReservationId(@Validated @ModelAttribute("item") ReservationForm item, BindingResult result) {
        if (result.hasErrors()) {
            return "reservation_form";
        }

    	var dltreservation = reservationRepository.findById(item.getId()).get();
    	if(dltreservation.getDeleteDateTime()==null) {
	    	dltreservation.setDeleteDateTime(LocalDateTime.now());
	    	reservationRepository.save(dltreservation);
    	}
        return "redirect:/reservation";
    }

	@GetMapping("/reservation/cancel")
	public String cancelReservation() {
		return "redirect:/reservation";
	}


    // --------------------------------------------------------------------------------
    // 営業時間
    // --------------------------------------------------------------------------------
    @GetMapping("/system")
    public String systemForm(Model model) {
        List<BusinessHour> bhlist = businessHourRepository.findAllByOrderById();
        List<TemporaryBusiness> tblist = temporaryBusinessRepository.findByDeleteDateTimeIsNull();
        BusinessHourForm bhf = new BusinessHourForm(bhlist,tblist);
    	model.addAttribute("bhf", bhf);
    	model.addAttribute("tempobj", new TemporaryBusiness()); //追加臨時営休業日用
    	model.addAttribute("tempday", timeListService.makeTempDayList()); //追加臨時営休業日の重複日防止用
    	model.addAttribute("permissionHour", permissionHourRepository.findById(1L).get());
        return "system_form";
    }

    //通常営業日時設定
    @PostMapping("/system/update")
    public String systemUpdate(@Validated @ModelAttribute("bhf") BusinessHourForm bhf, BindingResult result, Model model) {
        bhf.setTemporaryBusiness( temporaryBusinessRepository.findByDeleteDateTimeIsNull() ); //bhfの内List<TemporaryBusiness>が持ち越せないので
        model.addAttribute("bhf", bhf);
    	model.addAttribute("tempobj", new TemporaryBusiness()); //追加臨時営休業日用
    	model.addAttribute("tempday", timeListService.makeTempDayList()); //追加臨時営休業日の重複日防止用
    	model.addAttribute("permissionHour", permissionHourRepository.findById(1L).get());
        if (result.hasErrors()) {
            return "system_form";
        }
        businessHourRepository.saveAll(bhf.getBusinessHours());
        model.addAttribute("toast_message", "通常営業日時の更新");
        return "system_form";
    }

    //臨時営業日時設定 追加
    @PostMapping("/system/temp")
    public String systemTemp(@Validated @ModelAttribute("tempobj") TemporaryBusiness tempobj, BindingResult result, Model model) {
        List<BusinessHour> bhlist = businessHourRepository.findAllByOrderById();
        List<TemporaryBusiness> tblist = temporaryBusinessRepository.findByDeleteDateTimeIsNull();
        BusinessHourForm bhf = new BusinessHourForm(bhlist,tblist);
    	model.addAttribute("bhf", bhf);
    	model.addAttribute("tempday", timeListService.makeTempDayList());
    	model.addAttribute("permissionHour", permissionHourRepository.findById(1L).get());
        if (result.hasErrors()) {
            return "system_form";
        }
        temporaryBusinessRepository.save(tempobj);
        bhf.setTemporaryBusiness(temporaryBusinessRepository.findByDeleteDateTimeIsNull());
        model.addAttribute("bhf", bhf);
        model.addAttribute("tempobj", new TemporaryBusiness()); //追加臨時営休業日用
        model.addAttribute("tempday", timeListService.makeTempDayList());
        model.addAttribute("toast_message", "臨時営業日時を追加");
        return "system_form";
    }

    //臨時営業日時設定 削除
    @PostMapping("/system/temp/delete")
    public String systemTempDelete(@RequestParam Integer tempId, Model model) {
    	var dlttemp = temporaryBusinessRepository.findById(tempId).get();
    	if(dlttemp.getDeleteDateTime()==null) {
    		dlttemp.setDeleteDateTime(LocalDateTime.now());
    		temporaryBusinessRepository.save(dlttemp);
    	}

        List<BusinessHour> bhlist = businessHourRepository.findAllByOrderById();
        List<TemporaryBusiness> tblist = temporaryBusinessRepository.findByDeleteDateTimeIsNull();
        BusinessHourForm bhf = new BusinessHourForm(bhlist,tblist);
    	model.addAttribute("bhf", bhf);
    	model.addAttribute("tempobj", new TemporaryBusiness()); //追加臨時営休業日用
    	model.addAttribute("tempday", timeListService.makeTempDayList()); //追加臨時営休業日の重複日防止用
    	model.addAttribute("permissionHour", permissionHourRepository.findById(1L).get());
    	model.addAttribute("toast_message", "臨時営業日時を削除");
        return "system_form";
    }

    //予約可能時間とキャンセル可能時間
    @PostMapping("/system/update2")
    public String systemUpdate2(@ModelAttribute("permissionHour") PermissionHour permissionHour, Model model) {
    	permissionHourRepository.save(permissionHour);

        List<BusinessHour> bhlist = businessHourRepository.findAllByOrderById();
        List<TemporaryBusiness> tblist = temporaryBusinessRepository.findByDeleteDateTimeIsNull();
        BusinessHourForm bhf = new BusinessHourForm(bhlist,tblist);
    	model.addAttribute("bhf", bhf);
    	model.addAttribute("tempobj", new TemporaryBusiness()); //追加臨時営休業日用
    	model.addAttribute("tempday", timeListService.makeTempDayList()); //追加臨時営休業日の重複日防止用
    	model.addAttribute("permissionHour", permissionHourRepository.findById(1L).get());
    	model.addAttribute("toast_message", "予約・キャンセル可能時間の更新");
        return "system_form";
    }

}
