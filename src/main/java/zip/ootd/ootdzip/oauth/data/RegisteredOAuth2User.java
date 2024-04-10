package zip.ootd.ootdzip.oauth.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import zip.ootd.ootdzip.user.domain.User;

@Getter
@EqualsAndHashCode
@ToString
public class RegisteredOAuth2User implements OAuth2User {

    private final Set<GrantedAuthority> authorities;

    private final Map<String, Object> attributes;

    private final User user;

    public RegisteredOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes,
            User user) {
        this.authorities = authorities != null ?
                Collections.unmodifiableSet(new LinkedHashSet<>(this.sortAuthorities(authorities))) :
                Collections.unmodifiableSet(new LinkedHashSet<>(AuthorityUtils.NO_AUTHORITIES));
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
        this.user = user;
    }

    public String getName() {
        return user.getId().toString();
    }

    private Set<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(
                Comparator.comparing(GrantedAuthority::getAuthority));
        sortedAuthorities.addAll(authorities);
        return sortedAuthorities;
    }
}
