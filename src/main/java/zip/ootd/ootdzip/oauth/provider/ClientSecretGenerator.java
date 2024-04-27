package zip.ootd.ootdzip.oauth.provider;

public interface ClientSecretGenerator {

    String getRegistrationId();

    String generate();
}
