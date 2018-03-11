package info.investdigital.common;

/**
 * @author ccl
 * @time 2018-03-07 11:23
 * @name Const
 * @desc:
 */
public interface Const {
    enum ROLE implements Const{
        ADMIN(1L,"管理员"),USER(2L,"普通用户"),V(3L,"大V用户");

        private Long id;
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        ROLE(Long id, String name) {
            this.id=id;
            this.name=name;
        }
    }

     enum APPLYV implements Const{
        APPLIED(1),CANCELED(3),APPROVED(3),REJECTED(4);

        private Integer status;
         APPLYV(Integer status) {
             this.status = status;
         }

         public Integer getStatus() {
             return status;
         }

         public void setStatus(Integer status) {
             this.status = status;
         }
     }
}
