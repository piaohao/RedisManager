package org.piaohao.redisManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Session implements Serializable {
    private Integer id;
    private String address;
    private Integer port;
    private String password;
    private String remark;

    public Session(String address, Integer port, String password, String remark) {
        this.address = address;
        this.port = port;
        this.password = password;
        this.remark = remark;
    }
}
