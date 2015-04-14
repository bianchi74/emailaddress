package uk.gov.hmrc.emailaddress

import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.emailaddress.EmailAddress.{Mailbox, Domain}

class EmailAddressSpec extends WordSpec with Matchers with PropertyChecks with EmailAddressGenerators {

  "Creating an EmailAddress class" should {
    "work for a valid email" in {
      forAll (validEmailAddresses) { address =>
        EmailAddress(address).value should be(address)
      }
    }

    "throw an exception for an invalid email" in {
      an [IllegalArgumentException] should be thrownBy { EmailAddress("sausages") }
    }

    "throw an exception for an empty email" in {
      an [IllegalArgumentException] should be thrownBy { EmailAddress("") }
    }

    "throw an exception for a repeated email" in {
      an[IllegalArgumentException] should be thrownBy { EmailAddress("test@domain.comtest@domain.com") }
    }

    "throw an exception when the '@' is missing" in {
      forAll { s: String => whenever(!s.contains("@")) {
        an[IllegalArgumentException] should be thrownBy { EmailAddress(s) }
      }}
    }
  }

  "An EmailAddress class" should {
    "implicitly convert to a String of the address" in {
      val e: String = EmailAddress("test@domain.com")
      e should be ("test@domain.com")
    }
    "toString to a String of the address" in {
      val e = EmailAddress("test@domain.com")
      e.toString should be ("test@domain.com")
    }
    "be obfuscatable" in {
      EmailAddress("abcdef@example.com").obfuscated.value should be("a****f@example.com")
    }
    "have a local part" in forAll (validMailbox, validDomain) { (mailbox, domain) =>
      val exampleAddr = EmailAddress(s"$mailbox@$domain")
      exampleAddr.mailbox should (be (a[Mailbox]) and have ('value (mailbox)))
      exampleAddr.domain should (be (a[Domain]) and have ('value (domain)))
    }
  }

  "A email address domain" should {
    "be extractable from an address" in forAll (validMailbox, validDomain) { (mailbox, domain) =>
      EmailAddress(s"$mailbox@$domain").domain should (be (a[Domain]) and have ('value (domain)))
    }
    "compare equal if identical" in forAll (validDomain, validMailbox, validMailbox) { (domain, mailboxA, mailboxB) =>
      val exampleA = EmailAddress(s"$mailboxA@$domain")
      val exampleB = EmailAddress(s"$mailboxB@$domain")
      exampleA.domain should equal (exampleB.domain)
    }
    "not compare equal if completely different" in forAll (validMailbox, validDomain, validDomain) { (mailbox, domainA, domainB) =>
      val exampleA = EmailAddress(s"$mailbox@$domainA")
      val exampleB = EmailAddress(s"$mailbox@$domainB")
      exampleA.domain should not equal exampleB.domain
    }
  }

  "A email address mailbox" should {
    "be extractable from an address" in forAll (validMailbox, validDomain) { (mailbox, domain) =>
      EmailAddress(s"$mailbox@$domain").mailbox should (be (a[Mailbox]) and have ('value (mailbox)))
    }
    "compare equal" in forAll (validMailbox, validDomain, validDomain) { (mailbox, domainA, domainB) =>
      val exampleA = EmailAddress(s"$mailbox@$domainA")
      val exampleB = EmailAddress(s"$mailbox@$domainB")
      exampleA.mailbox should equal (exampleB.mailbox)
    }
    "not compare equal if completely different" in forAll (validDomain, validMailbox, validMailbox) { (domain, mailboxA, mailboxB) =>
      val exampleA = EmailAddress(s"$mailboxA@$domain")
      val exampleB = EmailAddress(s"$mailboxB@$domain")
      exampleA.mailbox should not equal exampleB.mailbox
    }
  }
}
