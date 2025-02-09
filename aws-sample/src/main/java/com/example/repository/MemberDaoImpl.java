package com.example.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.data.MemberQuery;
import com.example.entity.Member;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class MemberDaoImpl implements MemberDao {

	  private final EntityManager entityManager;

	  // JPQLによる検索
	  @Override
	  public Page<Member> findByJPQL(MemberQuery memberQuery, Pageable pageable) {

	    StringBuilder sb = new StringBuilder("select m from Member m where 1 = 1");
	    List<Object> params = new ArrayList<>();
	    int pos = 0;

	    // 実行するJPQLの組み立て
	    // 会員名
	    if (memberQuery.getName()!=null &&  memberQuery.getName().length() > 0) {
	      sb.append(" and m.name like ?" + (++pos));
	      params.add("%" + memberQuery.getName() + "%");
	    }

	    // E-mail
	    if (memberQuery.getEmail() != null) {
	      sb.append(" and m.email like ?" + (++pos));
	      params.add("%" + memberQuery.getEmail() + "%");
	    }

	    // ID
	    if (memberQuery.getId() != null) {
	      sb.append(" and m.id = ?" + (++pos));
	      params.add(memberQuery.getId());
	    }

	    // (退会していない)論理削除されていない
	    //sb.append(" and m.deleteDateTime IS NULL");


	    String sort = pageable.getSort().toString();
	    String[] sorts = sort.split(":");
	    sb.append(" order by " + sorts[0] + sorts[1]);
	    if (!(sorts[0].equals("id"))) {
	    	sb.append(", id" + sorts[1]); //name等の同名ソートで順番が逆転する事象の対策。最後に必ずidでソート
	    }								// name:田中太郎 id:1と、name:田中太郎 id:2があった場合後者の方が先になってしまうことがある

	    // クエリー生成
	    Query query = entityManager.createQuery(sb.toString());
	    for (int i = 0; i < params.size(); ++i) {
	      query = query.setParameter(i + 1, params.get(i));
	    }
	    // 総レコード数取得設定
	    int totalRows = query.getResultList().size();
	    // 先頭レコードの位置設定
	    query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
	    // １ページあたりの件数
	    query.setMaxResults(pageable.getPageSize());

	    @SuppressWarnings("unchecked")
		Page<Member> page = new PageImpl<Member>(query.getResultList(), pageable, totalRows);
	    return page;
	  }
}
