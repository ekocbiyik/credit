**Proje dizinine ait genel bilgi**
  * JAVA 17, Springboot 2.7.6 ve MAVEN kullanılarak geliştirilmiştir.
  * H2 veritabanı kullanılmıştır.
  * ``controller``  : API tanımlarının bulunduğu dizindir.
  * ``service``     : iş akış işlemlerinin gerçekleştirildiği dizindir.
  * ``repository``  : Veritabanı işlemlerinin gerçekleştirildiği dizindir.
  * ``model``       : DAO ve DTO modellerine ait tanımların yer aldığı dizindir.
  * ``exception``   : Hata durumlarını yakalayan ve anlamlı şekilde response dönmeyi sağlayan tanımların yer aldığı dizindir.
  * ``security``    : API erişimlerinin ve yetki kontrollerinin yapıldığı dizindir.
  * ``utils``       : projenin ilk çalıştırılmasında tablolar için örnek veri seti hazırlayan dizindir.


**Tablolar ve DB**


* DB arayüzü için uygulamayı çalıştırdıktan sonra browserdan `http://localhost:8080/h2-console` adresine gidilerek erişim sağlanabilir. 


* `User`
  * Bu tablo sistemin oturum/yetki kontrollerini gerçekleştirir, üyelik işlemleri şeklinde düşünülebilir.
  * Kullanıcılara ait ROLE_ADMIN ve ROLE_CUSTOMER yetkileri vardır.
  * Bir User ROLE_CUSTOMER yetkisine sahipse Customer tablosunda bir müşteri kimliği oluşturulmalıdır.


* `Role`
  * Sistemde bulunan yetki/rolleri ifade eder.
  * Basit düşünüldüğü için API erişim yetkileri roller üzerinden sağlandı. 


* `CustomerInfo`
  * Müşteri bilgilerinin ve kredi limitlerinin yer aldığı tablodur.
  * Bir User'a bağlı CustomerInfo olmayabilir.
  * Bir customerInfo mutlaka bir adet User'a bağlı olmak zorundadır.
  * Kredi işlemleri User'a ait CustomerInfo hesapları üzerinden sağlanacak.


* `Loan`
  * User'a ait kredi kullanım bilgilerinin yer aldığı tablodur.
  * bu tabloda belirtilen `loanAmount` müşteriye verilen kredi tutarını ifade eder.
  * bu tabloda belirtilen `totalAmount` müşterinin geri ödediği tutarı ifade eder. Bu bilgi gecikmeli ödemelerde veya erken ödemelerden kaynaklı tutar farkının kayıt altına alınması içindir.
  * bu tabloda belirtilen `interestRate` faiz oranını ifade eder.
  * bu tabloda belirtilen `numberOfInstallments` kredi taksit sayısını ifade eder.

* LoanInstallment
  * Krediye ait vade bilgilerinin tutulduğu tablodur.
  * `loanId` bilgisi hangi krediye ait olduğunu ifade eder.
  * `amount` krediye ait ödenmesi gereken tutarı ifade eder.
  * `paidAmount` müşterinin ödediği tutarı ifade eder. Vade erken ya da geç ödenmesinden kaynaklı fiyat farkının kayıt altına alınması için bu bilgi tabloya eklenmiştir.
  * `bonus` alanı; vadenin erken ya da geç ödenmesinden kaynaklanan farkın tutulduğu alandır. 
    * negatif değerde olması vade tarihi gelmeden ödeme yapıldığını gösterir.
    * pozitif değerde olması vade tarihi geçmiş olduğunu ifade eder.
    * sıfır olduğu durumlar gününde ödenen vade miktarını ifade eder.
    * `amount = paidAmount-bonus`
  * `dueDate` vade son ödeme tarihini ifade eder.
  * `paymentDate` ödemenin yapıldığı tarihi ifade eder.
  * `installmentOrder` vadenin sırasını ifade eder.


**Uygulama Hakkında**

* ROLE_ADMIN ve ROLE_CUSTOMER yetki tipine sahip iki tip kullanıcı vardır. Kullanıcı sayısı arttırılabilir.
* `SecurityConfig` class içinde gerekli role bazlı API erişimleri kontrol edilmektedir.
* `ROLE_ADMIN`
  * kullanıcı oluşturabilir, listeleyebilir
  * customerInfo oluşturabilir, listeleyebilir
  * Kredi oluşturabilir, listeleyebilir, bir kullanıcı adına ödeme yapabilir
* `ROLE_CUSTOMER`
  * Sadece kendine ait oluşturulan kredi bilgilerini listeleyebilir
  * Kendisine ait kredi tutarı için ödeme yapabilir
* Uygulama içerisinde alınan hatalar `RestControllerExceptionHandler` tarafından yakalanır, anlamlı bir hata kodu ve hata mesajı oluşturularak response edilir.
* Proje dizininde yer alan `CreditModulePostman.json` dosyası API kullanımları için gerekli postman collection dosyasıdır.
* Proje dizininde yer alan `index.html` dosyası API'lere ait kullanımı kolaylaştıracak basit bir arayüzü içerir. Testler için kullanılabilir.
