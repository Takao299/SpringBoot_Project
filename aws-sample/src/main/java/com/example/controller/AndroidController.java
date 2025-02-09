package com.example.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.data.CalendarDaysJsonConverter;
import com.example.data.FacilityAndDay;
import com.example.data.FacilityData;
import com.example.data.HistoryData;
import com.example.data.LoginData;
import com.example.data.MailCodeData;
import com.example.data.ReservationForm;
import com.example.data.ResultJsonConverter;
import com.example.data.SessionData;
import com.example.data.TimeBlock;
import com.example.data.UpdatePassData;
import com.example.entity.AttachedFile;
import com.example.entity.Facility;
import com.example.entity.MailCode;
import com.example.entity.Member;
import com.example.entity.Reservation;
import com.example.entity.SessionId;
import com.example.info.AndroidInfo;
import com.example.info.S3Info;
import com.example.repository.AttachedFileRepository;
import com.example.repository.FacilityRepository;
import com.example.repository.MailCodeRepository;
import com.example.repository.MemberRepository;
import com.example.repository.PermissionHourRepository;
import com.example.repository.ReservationRepository;
import com.example.repository.SessionIdRepository;
import com.example.service.AttachedFileService;
import com.example.service.MailSenderService;
import com.example.service.S3Service;
import com.example.service.MailSenderService.Title;
import com.example.service.TimeListService;
import com.example.validator.CustomLocalValidatorFactoryBean;
import com.example.validator.OverNonCancellableTimeValidator;
import com.example.validator.OverTodaysTimeValidator;
import com.example.validator.OverlapFacilityValidator;
import com.example.validator.OverlapMemberValidator;
import com.example.validator.TimeStartEndValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.RequiredArgsConstructor;

@RequestMapping("/android")
@RequiredArgsConstructor
@RestController
public class AndroidController {

	private final MemberRepository memberRepository;
	private final FacilityRepository facilityRepository;
	private final AttachedFileRepository attachedFileRepository;
	private final ReservationRepository reservationRepository;
	private final PermissionHourRepository permissionHourRepository;
	private final SessionIdRepository sessionIdRepository;
	private final MailCodeRepository mailCodeRepository;
	private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;
    private final TimeListService timeListService;
    private final AttachedFileService attachedFileService;
    private final S3Service s3Service;
    private final S3Info s3Info;
    private final AndroidInfo androidInfo;

    //ログイン
	@PostMapping("/login")
	public String androidLogin(@RequestBody String json) {
		//ログイン失敗のエラーをセキュリティのため一つにまとめる
		String result_json = "{\"userId\":-1,\"displayName\":\"no_data\"}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            Member _member = mapper.readValue(json, Member.class);
            Optional<Member> exist = memberRepository.findByEmailAndDeleteDateTimeIsNull(_member.getEmail());
            if(exist==null || exist.isEmpty())  //ここ無しでNoSuchElementExceptionに投げても良い
            	//入力メアドが存在しない場合
            	return result_json;

            Member member = exist.get();
        	if( passwordEncoder.matches(_member.getPassword(), member.getPassword()) ){
            	//パスワードが一致した //matches(CharSequenceSE rawPassword, StringSE encodedPassword)

        		//会員にまだ有効なセッションが存在していれば全て無効化する
        		List<SessionId> sessions = sessionIdRepository.findByMemberIdAndActiveIsTrue(member.getId());
        		for(SessionId s:sessions) {
        			disableSession(s);
        		}

            	//セッションIDを発行する
            	result_json = mapper.writeValueAsString( createSession(member, true) );

            }else {
            	//パスワード不一致
            	//result_json = "{\"id\":-1,\"displayName\":\"no_pass\"}";
            }

		} catch (NoSuchElementException e) {
			e.printStackTrace();
			//result_json = "{\"id\":-1,\"displayName\":\"no_email\"}";
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return result_json;
	}

	//ログインに必要なセッションと会員情報を詰める
	public LoginData createSession(Member member, boolean isLogin) throws JsonProcessingException {

		Long day = androidInfo.getDay()==null ? 1L : androidInfo.getDay(); //ログイン済セッションはデフォルト１日
		Long hour = androidInfo.getHour()==null ? 0L : androidInfo.getHour();
		Long minute = androidInfo.getMinute()==null ? 0L : androidInfo.getMinute();
		Long pageHour = androidInfo.getPageHour()==null ? 0L : androidInfo.getPageHour();
		Long pageMinute = androidInfo.getPageMinute()==null ? 10L : androidInfo.getPageMinute(); //メール認証はデフォルト10分

		LocalDateTime expirationDateTime = LocalDateTime.now().plusDays(day).plusHours(hour).plusMinutes(minute);
		//System.out.println("expirationDateTime:"+expirationDateTime);

		if(!isLogin) {
			expirationDateTime = LocalDateTime.now().plusHours(pageHour).plusMinutes(pageMinute);
			member.setId(-1L);
		}

    	String num = UUID.randomUUID().toString();

    	SessionId sessionId = new SessionId();
    	sessionId.setSessionId(num);
    	sessionId.setMemberId(member.getId());
    	sessionId.setActive(true);
    	sessionId.setExpirationDateTime(expirationDateTime);
    	sessionId.setCreateDateTime(LocalDateTime.now());
    	sessionId.setIssuer("test_admin");
    	sessionIdRepository.save(sessionId);

    	//受け渡すデータを作成
    	LoginData loginData = new LoginData();
    	loginData.setSessionId(num);
        loginData.setUserId(member.getId().toString());
        loginData.setDisplayName(member.getName());

        return loginData;

	}


	//メールアドレス登録（新規）
    @PostMapping("/mail_register")
    public String mailRegist(@RequestBody String json) {

		String result_json = "{\"userId\":-1,\"displayName\":\"no_data\"}";
        try {
        	ObjectMapper mapper = new ObjectMapper();

        	//メール登録後、次の画面だけで使えるセッションを発行する
        	//セキュリティのため、メールアドレスの重複に問わずセッションを返信する
        	LoginData loginData = createSession(new Member(), false);
        	result_json = mapper.writeValueAsString( loginData );

        	//有効な既存会員が存在するか
            Member _member = mapper.readValue(json, Member.class);
            Optional<Member> exist = memberRepository.findByEmailAndDeleteDateTimeIsNull(_member.getEmail());
        	if( exist.isEmpty() ) {
        		//存在しない
            	//認証コード発行
                Random rand = new Random();
                String randomCode = ""+rand.nextInt(10)+rand.nextInt(10)+rand.nextInt(10)+rand.nextInt(10); //４桁の数字

                MailCode mailCode = new MailCode();
                mailCode.setEmail(_member.getEmail());
                mailCode.setCode(randomCode);
                mailCode.setEnabled(false);
                mailCode.setSessionId(loginData.getSessionId()); //ページセッションをセット

        		//入力メールアドレスを保存
        		mailCodeRepository.save(mailCode);
           		//入力メールアドレスに認証用メールを送信
        		mailSenderService.sendVerifyMail(_member.getEmail(), randomCode);
        	}
		} catch (NoSuchElementException | IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result_json;
    }


    //メールアドレス登録（更新）
    @PostMapping("/update_mail")
    public String updateMail(@RequestBody String json) {

		String result_json = "{\"userId\":-1,\"displayName\":\"no_data\"}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            Member _member = mapper.readValue(json, Member.class);

        	//次の画面だけで使えるセッションを発行する
        	LoginData nextPage = createSession(new Member(), false);
        	result_json = mapper.writeValueAsString( nextPage );

            //更新希望メールアドレスの重複登録チェック
            Optional<Member> exist = memberRepository.findByEmailAndDeleteDateTimeIsNull(_member.getEmail());
        	if( exist.isEmpty() ) {
        		//未登録
            	//認証コード発行
                Random rand = new Random();
                String randomCode = ""+rand.nextInt(10)+rand.nextInt(10)+rand.nextInt(10)+rand.nextInt(10); //４桁の数字

                MailCode mailCode = new MailCode();
                mailCode.setEmail(_member.getEmail());
                mailCode.setCode(randomCode);
                mailCode.setEnabled(false);
                mailCode.setSessionId(nextPage.getSessionId());

                //入力メールアドレスを保存
        		mailCodeRepository.save(mailCode);
        		//入力メールアドレスに認証用メールを送信
        		mailSenderService.sendVerifyMail(_member.getEmail(), randomCode, _member.getName(), Title.Update); //更新用

        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result_json;
    }


    //メールアドレス登録（新規）コード認証
    @PostMapping("/code_register")
    public String codeRegist(@RequestBody String json) {
		String result_json = "{\"result\":\"no_data\"}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            MailCodeData data = mapper.readValue(json, MailCodeData.class);

            //ページセッションチェック
            if( !checkSession(data.getPageSessionId(), false) )
            	return result_json = "{\"result\":\"no_session\"}";

            //コードチェック
            Optional<MailCode> _mailCode =
            		mailCodeRepository.findByEmailAndCodeAndSessionId(
            				data.getEmail(),
            				data.getCode(),
            				data.getPageSessionId());
            if(!_mailCode.isEmpty()) {
            	//認証成功
            	result_json = "{\"result\":\"success\"}";
            	//認証済保存（意味ないかも）
            	MailCode mailCode = _mailCode.get();
            	mailCode.setEnabled(true);
            	mailCodeRepository.save(mailCode);
            }else {
            	//認証失敗
            	result_json = "{\"result\":\"no_code\"}";
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return result_json;
    }


    //会員登録
    @PostMapping("/form_register")
    public String formRegist(@RequestBody String json) {
		String result_json = "{\"result\":\"no_data\"}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            Member _member = mapper.readValue(json, Member.class);

            //同じメールアドレスで既に有効な会員が登録されていないか
            	//複数のデバイスから同一メールアドレスで複数の会員登録画面を開いて登録ボタンを押しても、ここで失敗する
            Optional<Member> exist = memberRepository.findByEmailAndDeleteDateTimeIsNull(_member.getEmail());
            if( exist.isEmpty() ) {
            	//該当会員なし 正式に会員登録へ
            	_member.setPassword( passwordEncoder.encode(_member.getPassword()) ); //暗号化
    			memberRepository.save(_member);
        		result_json = "{\"result\":\"success\"}";
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return result_json;
    }


    //メールアドレス登録（更新）コード認証と会員登録
    @PostMapping("/update_mail_code_register")
    public String updateMailRegistCode(@RequestBody String json) {
		String result_json = "{\"result\":\"no_data\"}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            MailCodeData data = mapper.readValue(json, MailCodeData.class);

            //ページセッションチェック
            //ログインセッションチェック
            if( !checkSession(data.getPageSessionId(), false) || !checkSession(data.getLoginSession(), false) )
            	return result_json = "{\"result\":\"no_session\"}";

            //コードチェック
            Optional<MailCode> _mailCode =
            		mailCodeRepository.findByEmailAndCodeAndSessionId(
            				data.getEmail(), //更新後のメアド
            				data.getCode(),
            				data.getPageSessionId());
            if(!_mailCode.isEmpty()) {
            	//認証成功
            	//認証済保存
            	MailCode mailCode = _mailCode.get();
            	mailCode.setEnabled(true);
            	mailCodeRepository.save(mailCode);

            	//続けてメールアドレス更新実行
                //更新後のメールアドレスで既に有効な会員が登録されていないか
                Optional<Member> exist =
                		memberRepository.findByEmailAndDeleteDateTimeIsNull(data.getEmail());
                if( exist.isEmpty() ) {
                	//該当会員なし　正式に会員登録へ
                	//ログイン中の会員IDを元にDBから更新前の会員情報を持ってくる
                	Long member_id = Long.valueOf(data.getLoginSession().getMemberId());
                	Member member = memberRepository.findById(member_id).get();
                	member.setEmail(data.getEmail());
        			memberRepository.save(member);
            		result_json = "{\"result\":\"success\"}";
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return result_json;
    }


    //施設選択画面
	@GetMapping("/facility")
	public String showFacilityList() throws JsonProcessingException {
        List<Facility> facilities = facilityRepository.findByDeleteDateTimeIsNullOrderByIdAsc();
        List<FacilityData> fdList = new ArrayList<>();
        for(Facility f:facilities) {
        	FacilityData fd = new FacilityData();
        	fd.setId(f.getId());
        	fd.setName(f.getName());
        	fd.setAmount(f.getAmount());
        	fd.setMemo(f.getMemo());
        	fdList.add(fd);
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(fdList);
		return json;
	}


	//カレンダーを表示
	@GetMapping("/calendarDays")
	public String makeCalendarDays() throws JsonProcessingException {
		CalendarDaysJsonConverter calendarDays =
				new CalendarDaysJsonConverter(
						timeListService.makeOffDayList2(),
						timeListService.makeTempOnDayList(),
						timeListService.makeTempOffDayList());
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(calendarDays);
		return json;
	}


	//カレンダーの日付を選択 時間リストを返信
    @PostMapping("/reservation/select_day")
    public String selectDay(@RequestBody String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        FacilityAndDay fd = mapper.readValue(json, FacilityAndDay.class);
        Facility facility = facilityRepository.findByIdAndDeleteDateTimeIsNull(fd.getFacility_id()).get();
    	LocalDate rday = LocalDate.parse(fd.getRday());

    	//選択された施設と予約日から予約可能な時間リストを作成
    	List<TimeBlock> tbs = timeListService.makeTimeList(facility, rday);
        String result_json = mapper.writeValueAsString(tbs);
		return result_json;
    }


    //予約をする
    @Transactional
    @PostMapping("/reservation/create")
    public String createReservation(@RequestBody String json) {
    	String result_json = "{\"result\":\"no_data\"}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            Reservation reservation = mapper.readValue(json, Reservation.class);

            //ReservationFormに変換してバリデーションチェックする
        	Member member = memberRepository.findById(reservation.getMemberId()).get();
        	Facility facility = facilityRepository.findById(reservation.getFacilityId()).get();
            ReservationForm reservationForm = new ReservationForm(reservation, member, facility);

        	//バリデーションチェック
            Set<ConstraintViolation<Object>> violations = myValidate2(reservationForm);
            if ( !violations.isEmpty() ) {
                // バリデーションエラー 全てのエラーメッセージをjsonに詰め込む
            	result_json = ObjectConvertToJson(violations);
            }else {
            	//予約を完了する
            	reservationRepository.save(reservation);
            	result_json = "{\"result\":\"success\"}";
            }
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException | NoSuchElementException | IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return result_json;
    }

    //バリデーション用メソッド
    private Set<ConstraintViolation<Object>> myValidate(Object form) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(form);
        return violations;
    }

    //バリデーション用メソッド2 ReservationForm用 （上のメソッドではクラス内の〇〇Repositoryがnullになってしまうため作成）
    private Set<ConstraintViolation<Object>> myValidate2(Object form) {
        List<ConstraintValidator<?,?>> customConstraintValidators = new ArrayList<>();
                //Collections.singletonList(new OverlapMemberValidator(reservationRepository));
        		Collections.addAll(customConstraintValidators,
        				new TimeStartEndValidator(),
        				new OverlapMemberValidator(reservationRepository),
        				new OverlapFacilityValidator(reservationRepository),
        				new OverTodaysTimeValidator(permissionHourRepository),
        				new OverNonCancellableTimeValidator(permissionHourRepository));
        ValidatorFactory customValidatorFactory = new CustomLocalValidatorFactoryBean(customConstraintValidators);
        Validator validator = customValidatorFactory.getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(form);
        return violations;
    }

    //Javaオブジェクト → JSONデータへの変換
    //複数行に渡るエラーメッセージオブジェクト用
    public String ObjectConvertToJson(Set<ConstraintViolation<Object>> violations){

        List<ResultJsonConverter.Error> errors = new ArrayList<>();
        for(ConstraintViolation<Object> c: violations) {
            ResultJsonConverter.Error error = new ResultJsonConverter.Error();
            error.setMessage(c.getMessage());
            errors.add(error);
        }

    	ResultJsonConverter jsonConverter = new ResultJsonConverter();
        jsonConverter.setResult("error");
        jsonConverter.setErrors(errors);

        // オブジェクトからJSONに変換するためObjectMapperをインスタンス化する.
        // 「.enable()...」 以降はJSONデータをコンソール出力する際、見やすく改行した状態で出力するための呪文.
        //(追記)改行するので受け取る方は複数行読み取ること
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        String json;
        try {
            // writeValueAsStringメソッドでJavaオブジェクトをJSONに変換する.
            json = mapper.writeValueAsString(jsonConverter);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }


    //セッション無効化
    public void disableSession(SessionId sessionId) {
    	sessionId.setActive(false);
		sessionIdRepository.save(sessionId);

    }

    //セッション処理
    public boolean checkSession(SessionData sessionData, boolean logout) { //第2引数はログアウト処理かどうか

        Long member_id;
        try {
        	member_id = Long.parseLong(sessionData.getMemberId()); //数字じゃなかった場合、強制的に「-1」にする
        }catch (NumberFormatException e){
        	member_id = -1L;
        }

    	Optional<SessionId> session_op = sessionIdRepository.findBySessionId(sessionData.getSessionId());
    	if(session_op==null || session_op.isEmpty())
    		return false;
    	SessionId session = session_op.get();
    	//有効かどうか、発行者が正しいか、セッションの本来の所持者かどうか
    	if(!session.isActive() || !session.getIssuer().equals("test_admin") || session.getMemberId()!=member_id)
    		return false;
    	//期限切れであれば無効化、またログアウト処理なら強制的に無効化
    	if(session.getExpirationDateTime().isBefore(LocalDateTime.now()) || logout ) {
    		disableSession(session);
    		return false;
    	}
    	return true;
    }

    //ページセッション用
    public boolean checkSession(String sessionId, boolean logout) {
        SessionData sessionData = new SessionData();
        sessionData.setMemberId("-1");
        sessionData.setSessionId(sessionId);
        return checkSession(sessionData,logout);
    }


    //セッションチェック
    @PostMapping("/checkSession")
    public String checkJsonSession(@RequestBody String json) {

		String result_json = "{\"result\":\"no_data\"}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            SessionData sessionData = mapper.readValue(json, SessionData.class);

            result_json = "{\"result\":\"false\"}";
            if( checkSession(sessionData, false) )
            	result_json = "{\"result\":\"true\"}";

		} catch (Exception e) {
			e.printStackTrace();
		}
        return result_json;
    }


    //ログアウト
    @PostMapping("/deleteSession")
    public String deleteJsonSession(@RequestBody String json) {

		String result_json = "{\"result\":\"no_data\"}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            SessionData sessionData = mapper.readValue(json, SessionData.class);

            //セッションを無効化する
            checkSession(sessionData, true);
            result_json = "{\"result\":\"disableSession\"}";

		} catch (Exception e) {
			e.printStackTrace();
		}
        return result_json;
    }


    //予約一覧（現在有効な予約のみ）
	@PostMapping("/remainList")
	public String showRemainList(@RequestBody String json) {

    	String result_json = "{\"result\":\"no_data\"}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            SessionData sessionData = mapper.readValue(json, SessionData.class);

            //セッションチェック
            result_json = "{\"result\":\"false\"}";
            if( checkSession(sessionData, false) ) {
            	result_json = "{\"result\":\"true\"}";
            }

            Member member = memberRepository.findById(Long.parseLong(sessionData.getMemberId())).get();
        	List<Reservation> list = reservationRepository.findRemainByMemberId(member.getId(),LocalDate.now(),LocalTime.now());
        	List<HistoryData> dataList = new ArrayList<>();
        	for(Reservation res:list) {
        		Facility facility = facilityRepository.findById(res.getFacilityId()).get();
        		HistoryData data = new HistoryData(res,member,facility);
        		dataList.add(data);
        	}
        	result_json = mapper.writeValueAsString(dataList);

		} catch (Exception e) {
			e.printStackTrace();
		}
        return result_json;
	}


    //予約一覧（過去の予約）
	@PostMapping("/pastList")
	public String showPastList(@RequestBody String json) {

    	String result_json = "{\"result\":\"no_data\"}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            SessionData sessionData = mapper.readValue(json, SessionData.class);

            //セッションチェック
            result_json = "{\"result\":\"false\"}";
            if( checkSession(sessionData, false) ) {
            	result_json = "{\"result\":\"true\"}";
            }

            Member member = memberRepository.findById(Long.parseLong(sessionData.getMemberId())).get();
        	List<Reservation> list = reservationRepository.findPastByMemberId(member.getId(),LocalDate.now(),LocalTime.now());
        	List<HistoryData> dataList = new ArrayList<>();
        	for(Reservation res:list) {
        		Facility facility = facilityRepository.findById(res.getFacilityId()).get();
        		HistoryData data = new HistoryData(res,member,facility);
        		dataList.add(data);
        	}
        	result_json = mapper.writeValueAsString(dataList);

		} catch (Exception e) {
			e.printStackTrace();
		}
        return result_json;
	}


	//予約をキャンセル
	@Transactional //これ付けないとsaveの所で例外が起こる
    @PostMapping("/r_cancel")
    public String cancelReservation(@RequestBody String json) {

    	String result_json = "{\"result\":\"no_data\"}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            Reservation _reservation = mapper.readValue(json, Reservation.class);
            Reservation reservation = reservationRepository.findById(_reservation.getId()).get();

            //不正？リクエストした会員IDと予約会員IDが異なる場合リターン
            if((long)_reservation.getMemberId() != reservation.getMemberId() )
            	return result_json = "{\"result\":\"dif_member_id\"}";

            //ReservationFormに変換してバリデーションチェックする
        	Member member = memberRepository.findById(reservation.getMemberId()).get();
        	Facility facility = facilityRepository.findById(reservation.getFacilityId()).get();
            ReservationForm reservationForm = new ReservationForm(reservation, member, facility);

            //バリデーションチェック
            Set<ConstraintViolation<Object>> violations = myValidate2(reservationForm);
            if ( !violations.isEmpty() ) {
                // バリデーションエラー 全てのエラーメッセージをjsonに詰め込む
            	result_json = ObjectConvertToJson(violations);
            }else {
            	//予約をキャンセル可能
            	if(reservation.getDeleteDateTime()==null) {
            		//キャンセルを完了する
            		reservation.setDeleteDateTime(LocalDateTime.now());
        	    	reservationRepository.save(reservation);
        	    	result_json = "{\"result\":\"success\"}";
            	}else {
            		result_json = "{\"result\":\"alreadyCanceled\"}";
            		//複数デバイスで同じ予約の詳細画面を開いていた時到達？
            		//このメソッド内でセッションチェックできてないからあり得る
            		//ただ論理削除による更新処理なのでどの道@Versionで弾かれる
            	}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return result_json;
    }


    //会員情報画面
    @PostMapping("/member_show")
    public String memberShow(@RequestBody String json) {

		String result_json = "{\"id\":\"-1\"}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            SessionData sessionData = mapper.readValue(json, SessionData.class);
            //セッションチェック
            if( !checkSession(sessionData, false) )
            	return result_json = "{\"id\":\"-1\"}";

            Member member = memberRepository.findById(Long.parseLong(sessionData.getMemberId())).get();
            //MemberのID、email、nameだけ欲しいので他が余計。特にreservationListがOneToManyで無駄に参照している
            Member send_member = new Member();
            send_member.setId(member.getId());
            send_member.setEmail(member.getEmail());
            send_member.setName(member.getName());
            result_json = mapper.writeValueAsString(send_member);

		} catch (Exception e) {
			e.printStackTrace();
		}
        return result_json;
    }


    //パスワード変更
    @PostMapping("/update_pass")
    public String updatePass(@RequestBody String json) {
		String result_json = "{\"result\":\"no_data\"}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            UpdatePassData data = mapper.readValue(json, UpdatePassData.class);

            //セッションチェック
            if( !checkSession(data.getSessionData(), false) )
            	return result_json = "{\"result\":\"no_session\"}";

            //パスワードチェック
            Member member = memberRepository.findByEmailAndDeleteDateTimeIsNull(data.getEmail()).get();
            if( passwordEncoder.matches(data.getPassword0(), member.getPassword()) ){
                //現在パスワードが一致した //matches(CharSequenceSE rawPassword, StringSE encodedPassword)
            	member.setPassword( passwordEncoder.encode(data.getPassword0()) ); //暗号化
    			memberRepository.save(member); //パスワード更新

        		result_json = "{\"result\":\"success\"}";
            }else {
            	//現在パスワード入力が間違っている
            	result_json = "{\"result\":\"no_pass\"}";
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return result_json;
    }


    //その他会員情報変更
    @PostMapping("/update_etc")
    public String updateEtc(@RequestBody String json) {
		String result_json = "{\"result\":\"no_data\"}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            Member _member = mapper.readValue(json, Member.class);

            //セッションチェック無し @Versionで対処

            //バリデーションチェック
            Set<ConstraintViolation<Object>> violations = myValidate(_member);
            if ( !violations.isEmpty() ) {
                // バリデーションエラー 全てのエラーメッセージをjsonに詰め込む
            	result_json = ObjectConvertToJson(violations);
            }else {
                Member member = memberRepository.findByEmailAndDeleteDateTimeIsNull(_member.getEmail()).get();
                member.setName(_member.getName());
        		memberRepository.save(member);
        	    result_json = "{\"result\":\"success\"}";
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return result_json;
    }


    //会員退会
    @PostMapping("/delete_member")
    public String deleteMember(@RequestBody String json) {
		String result_json = "{\"result\":\"no_data\"}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            UpdatePassData data = mapper.readValue(json, UpdatePassData.class);

            //セッションチェック
            if( !checkSession(data.getSessionData(), false) )
            	return result_json = "{\"result\":\"no_session\"}";

            //予約が残っていないかチェック
            Member member = memberRepository.findByEmailAndDeleteDateTimeIsNull(data.getEmail()).get();
        	if(reservationRepository.findRemainByMemberId(member.getId(),LocalDate.now(),LocalTime.now()).size() > 0) {
        		result_json = "{\"result\":\"remain\"}";
        	}else {
        		//論理削除を実行
            	member.setDeleteDateTime(LocalDateTime.now());
        	    memberRepository.save(member);

        	    //セッションを削除
                checkSession(data.getSessionData(), true);

        		result_json = "{\"result\":\"success\"}";
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return result_json;
    }


    //サーバー画像存在チェック
	@GetMapping("/load_attached_files")
	public String loadAttachedFiles() throws JsonProcessingException {
        List<AttachedFile> files = attachedFileRepository.findAll();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(files);
		return json;
	}

	// Android用画像ダウンロード
    @GetMapping("/download_image/{imageName}")
    public ResponseEntity<Resource> downloadImageForAndroid(@PathVariable("imageName") String imageName) { //HttpEntity<byte[]>
    	if(s3Info.getBuckectName()==null || s3Info.getBuckectName().equals("no_bucket")) {
    		// ローカルストレージからファイルをダウンロード
    		return attachedFileService.download(imageName, null);
    	}else {
    		//AWS S3ストレージからファイルをダウンロード
    		return s3Service.download(imageName, null);
    	}
    }

}
