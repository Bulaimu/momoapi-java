package ci.bamba.regis;

import java.util.UUID;

import ci.bamba.regis.exceptions.RequestException;
import ci.bamba.regis.models.AccountBalance;
import ci.bamba.regis.models.AccountStatus;
import ci.bamba.regis.models.CollectionsRequestToPayBodyRequest;
import ci.bamba.regis.models.CollectionsRequestToPay;
import ci.bamba.regis.models.Token;
import io.reactivex.Observable;

public class Collections extends Product {

    Collections(String baseUrl, Environment environment, String subscriptionKey) {
        super(baseUrl, environment, subscriptionKey);
    }

    /**
     * @param apiUser an Api user
     * @param apiKey  an Api key
     * @return a token observable that you can use to authenticate your requests.
     */
    public Observable<Token> createToken(String apiUser, String apiKey) {
        return super.createToken("collection", apiUser, apiKey);
    }

    public Observable<Token> createToken() {
        return super.createToken("collection");
    }

    public Observable<Token> createToken(String providerCallbackHost) {
        return super.createToken(providerCallbackHost, "collection");
    }

    public Observable<String> requestToPay(String token, float amount, String currency, String externalId, String payerPartyId, String payerMessage, String payeeNote) {
        CollectionsRequestToPayBodyRequest body = new CollectionsRequestToPayBodyRequest(String.format("%s", amount), currency, externalId, payerPartyId, payerMessage, payeeNote);
        String authorization = String.format("Bearer %s", token);
        String referenceId = UUID.randomUUID().toString();
        return RestClient
                .getService(getBaseUrl())
                .collectionsCreateRequestToPay(authorization, getSubscriptionKey(), referenceId, getEnvironment().getEnv(), body)
                .map(response -> {
                    if (response.code() == 202) {
                        return referenceId;
                    } else {
                        throw new RequestException(response.code(), response.message());
                    }
                });
    }

    public Observable<CollectionsRequestToPay> getRequestToPay(String token, String referenceId) {
        String authorization = String.format("Bearer %s", token);
        return RestClient
                .getService(getBaseUrl())
                .collectionsGetRequestToPay(authorization, getSubscriptionKey(), getEnvironment().getEnv(), referenceId)
                .map(response -> {
                    if (response.code() == 200) {
                        return response.body();
                    } else {
                        throw new RequestException(response.code(), response.message());
                    }
                });
    }

    public Observable<AccountBalance> getAccountBalance(String token) {
        String authorization = String.format("Bearer %s", token);
        return RestClient.getService(getBaseUrl())
                .collectionsGetAccountBalance(authorization, getSubscriptionKey(), getEnvironment().getEnv())
                .map(response -> {
                    if (response.code() == 200) {
                        return response.body();
                    } else {
                        throw new RequestException(response.code(), response.message());
                    }
                });
    }

    public Observable<AccountStatus> getAccountStatus(String token, String msisdn) {
        String authorization = String.format("Bearer %s", token);
        return RestClient.getService(getBaseUrl())
                .collectionsGetAccountStatus(authorization, getSubscriptionKey(), getEnvironment().getEnv(), "msisdn", msisdn)
                .map(response -> {
                    if (response.code() == 200) {
                        return response.body();
                    } else {
                        throw new RequestException(response.code(), response.message());
                    }
                });
    }
}
