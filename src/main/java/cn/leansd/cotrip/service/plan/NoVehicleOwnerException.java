package cn.leansd.cotrip.service.plan;

public class NoVehicleOwnerException extends Exception {
    public NoVehicleOwnerException(String userId) {
        super("only vehicle owner can create hitchhiking provider trip plan. " + "user id: " + userId);
    }
}
